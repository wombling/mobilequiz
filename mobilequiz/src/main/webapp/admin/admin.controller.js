sap.ui.controller("admin.admin", {

	/**
	 * Called when a controller is instantiated and its View controls (if
	 * available) are already created. Can be used to modify the View before it
	 * is displayed, to bind event handlers and do other one-time
	 * initialization.
	 * 
	 * @memberOf mobilequizuser.admin
	 */
	onInit : function() {

		var questionModel = new sap.ui.model.json.JSONModel({});

		this.getView().setModel(questionModel);
		this.fnRefresh();

		this.createQuestionDialog = sap.ui.xmlfragment("admin.createQuestionDialog", this);
		this.getView().addDependent(this.createQuestionDialog);

		var createQuestionModel = new sap.ui.model.json.JSONModel({});
		this.createQuestionDialog.setModel(createQuestionModel);

		// websockets

		thisView = this;
		function url(s) {
			var l = window.location;
			return ((l.protocol === "https:") ? "wss://" : "ws://") + l.hostname + (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "")
					+ "/mobilequiz/" + s;
		}

		var socket = new WebSocket(url("questionWebSocket"));
		socket.onopen = function() {
			console.log('WebSocket connection is established');
		};

		socket.onmessage = function(messageEvent) {
			thisView._questionData = JSON.parse(messageEvent.data);
			thisView.fnLoadQuestionList(thisView._questionData);
			thisView.fnRenderGraph();
		};

		var config = {
			"showGraph" : false
		};

		var configModel = new sap.ui.model.json.JSONModel(config);

		this.getView().setModel(configModel, "cfg");

	},
	/**
	 * Similar to onAfterRendering, but this hook is invoked before the
	 * controller's View is re-rendered (NOT before the first rendering!
	 * onInit() is used for that one!).
	 * 
	 * @memberOf mobilequizuser.admin
	 */
	// onBeforeRendering: function() {
	//
	// },
	/**
	 * Called when the View has been rendered (so its HTML is part of the
	 * document). Post-rendering manipulations of the HTML could be done here.
	 * This hook is the same one that SAPUI5 controls get after being rendered.
	 * 
	 * @memberOf mobilequizuser.admin
	 */
	// onAfterRendering: function() {
	//
	// },
	/**
	 * Called when the Controller is destroyed. Use this one to free resources
	 * and finalize activities.
	 * 
	 * @memberOf mobilequizuser.admin
	 */
	// onExit: function() {
	//
	// }
	fnShowGraph : function(evt) {

		var sourcePath = evt.getSource().getBindingContext().getPath();
		this._indexInModel = parseInt(sourcePath.substring(14));

		var configModel = this.getView().getModel("cfg");

		var config = configModel.getProperty("/");

		config.showGraph = true;
		configModel.setData(config);

		this.fnRenderGraph();

	},

	fnRenderGraph : function() {

		var configModel = this.getView().getModel("cfg");

		var config = configModel.getProperty("/");

		if (config.showGraph) {

			var questionModel = this.getView().getModel();

			var data = [ {
				"vote" : "Yes",
				"number" : questionModel.oData.questionList[this._indexInModel].yesVotes
			}, {
				"vote" : "No",
				"number" : questionModel.oData.questionList[this._indexInModel].noVotes
			} ];
			var margin = {
				top : 20,
				right : 20,
				bottom : 10,
				left : 40
			}, width = 960 - margin.left - margin.right, height = 500 - margin.top - margin.bottom;

			var x = d3.scale.ordinal().rangeRoundBands([ 0, width ], .1);

			var y = d3.scale.linear().range([ height, 0 ]);

			var xAxis = d3.svg.axis().scale(x).orient("bottom");

			var yAxis = d3.svg.axis().scale(y).orient("left").ticks(10);

			var svg = d3.select("#admin--chart");

			svg.html("");

			svg.attr("width", width + margin.left + margin.right).attr("height", height + margin.top + margin.bottom).append("g").attr("transform",
					"translate(" + margin.left + "," + margin.top + ")");

			x.domain(data.map(function(d) {
				return d.vote;
			}));
			y.domain([ 0, d3.max(data, function(d) {
				return d.number;
			}) ]);

			svg.append("g").attr("class", "x axis").attr("transform", "translate(0," + height + ")").call(xAxis);

			svg.append("g").attr("class", "y axis").call(yAxis).append("text").attr("transform", "rotate(-90)").attr("y", 6).attr("dy", ".71em")
					.style("text-anchor", "end").text("Votes");

			svg.selectAll(".bar").data(data).enter().append("rect").attr("class", function(d) {
				return "bar bar-" + d.vote;
			}).attr("x", function(d) {
				return x(d.vote);
			}).attr("width", x.rangeBand()).attr("y", function(d) {
				return y(d.number);
			}).attr("height", function(d) {
				return height - y(d.number);
			});
		}
	},

	fnDeleteQuestion : function(evt) {

		var sourcePath = evt.getSource().getBindingContext().getPath();

		var questionModel = this.getView().getModel();

		var questionData = questionModel.getProperty(sourcePath);

		if (!this._busyDialog) {
			this._busyDialog = sap.ui.xmlfragment("admin.busyDialog", this);
			this.getView().addDependent(this._busyDialog);
		}

		this._busyDialog.open();
		var thisView = this;
		$.ajax({
			url : questionData.resourceLink,
			type : "DELETE",
			dataType : "json",
			async : true,
			success : reloadQuestions,
			error : function(xhr, ajaxOptions, thrownError) {

				var msg = "";
				switch (xhr.status) {
				case 404:
					msg = "Question not found, may already have been deleted";

				default:
					msg = xhr.statusText;
					break;
				}
				sap.m.MessageToast.show(msg);
				thisView._busyDialog.close();
				thisView.fnRefresh();
			}
		});

		function reloadQuestions() {
			thisView._busyDialog.close();
			thisView.fnRefresh();
		}

	},

	fnCreateQuestion : function() {
		this.createQuestionDialog.open();
	},

	fnSaveQuestion : function() {
		var createQuestionModel = this.createQuestionDialog.getModel();

		var data = createQuestionModel.getProperty("/");

		if (data.questionText == "" || data.secondsUntilExpiry == "") {
			sap.m.MessageToast.show("Please ensure all fields correctly populated");
		} else {
			if (!this._busyDialog) {
				this._busyDialog = sap.ui.xmlfragment("admin.busyDialog", this);
				this.getView().addDependent(this._busyDialog);
			}

			this._busyDialog.open();

			var thisView = this;
			$.ajax({
				url : "a/createQuestion",
				type : "POST",
				dataType : "json",
				contentType : "application/json",
				data : JSON.stringify(data),
				async : true,
				success : reloadQuestions,
				error : function(xhr, ajaxOptions, thrownError) {

					var msg = "";
					switch (xhr.status) {
					case 400:
						msg = "Question not created, check that seconds is a number (no text) greater than zero";

					default:
						msg = xhr.statusText;
						break;
					}
					thisView._busyDialog.close();
					sap.m.MessageToast.show(msg);
				}
			});
		}

		function reloadQuestions() {
			thisView._busyDialog.close();
			thisView.createQuestionDialog.close();
			thisView.fnRefresh();
		}

	},

	fnCloseDialog : function(evt) {
		evt.getSource().getParent().close();
	},
	fnRefresh : function() {

		var thisView = this;
		$.ajax({
			url : "a/listQuestions",
			type : "GET",
			dataType : "json",
			async : true,
			success : thisView.fnLoadQuestionList,
			error : function(xhr, ajaxOptions, thrownError) {

				var msg = "";
				switch (xhr.status) {

				default:
					msg = xhr.statusText;
					break;
				}

				sap.m.MessageToast.show(msg);
			}
		});
	},
	fnLoadQuestionList : function(data) {

		if (thisView._timeouts) {
			for (var i = 0; i < thisView._timeouts.length; i++) {
				clearTimeout(thisView._timeouts[i]);
			}
		}
		// quick reset of the timer array you just cleared
		thisView._timeouts = [];

		// sort data so latest at top

		data.questionList.sort(function(a, b) {
			var c = new Date(a.dateTimeCreated);
			var d = new Date(b.dateTimeCreated);
			return d - c;
		});

		var questionModel = thisView.getView().getModel();
		questionModel.setData(data);

		for (var i = 0; i < data.questionList.length; i++) {
			var t = new Date();
			t.setSeconds(t.getSeconds() + data.questionList[i].secondsRemaining);
			data.questionList[i].expireTime = t;
		}

		questionModel.setData(data);
		formatCountdown();

		function formatCountdown() {

			var data = questionModel.getProperty("/");
			var now = new Date();
			var countOnGoing = false;

			for (var i = 0; i < data.questionList.length; i++) {

				var secondsToExpire = Math.floor((questionModel.getProperty("/questionList/" + i + "/expireTime") - now) / 1000);

				if (secondsToExpire < 0) {
					secondsToExpire = 0;
				}

				var sec_num = parseInt(secondsToExpire, 10);
				var hours = Math.floor(sec_num / 3600);
				var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
				var seconds = sec_num - (hours * 3600) - (minutes * 60);

				if (hours < 10) {
					hours = "0" + hours;
				}
				if (minutes < 10) {
					minutes = "0" + minutes;
				}
				if (seconds < 10) {
					seconds = "0" + seconds;
				}
				var time = hours + ':' + minutes + ':' + seconds;

				questionModel.setProperty("/questionList/" + i + "/secondsRemaining", time);
				if (secondsToExpire > 0) {
					countOnGoing = true;
				}

			}
			return countOnGoing;
		}

		function decreaseTimer() {

			if (formatCountdown()) {
				thisView._timeouts.push(setTimeout(decreaseTimer, 1000));
			}
		}

		thisView._timeouts.push(setTimeout(decreaseTimer, 1000));

	}

});