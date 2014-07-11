sap.ui.controller("user.Question", {

	/**
	 * Called when a controller is instantiated and its View controls (if
	 * available) are already created. Can be used to modify the View before it
	 * is displayed, to bind event handlers and do other one-time
	 * initialization.
	 * 
	 * @memberOf user.Question
	 */
	onInit : function() {

		var config = {
			"showLoadingThingy" : true,
			"shakeSupported" : false,
			"shakeNotSupported" : false,
			"showQuestion" : false
		};

		var configModel = new sap.ui.model.json.JSONModel(config);

		this.getView().setModel(configModel, "cfg");
		this.getView().bindElement("cfg>/");
		var _e = null;
		var _i = null;
		var _c = null;

		var updateOrientation = function(e) {
			_e = e;
			window.removeEventListener("deviceorientation", updateOrientation, false);
		};

		var thisView = this;
		window.addEventListener("deviceorientation", updateOrientation, false);

		_i = window.setInterval(function() {
			if (_e !== null && _e.alpha !== null) {
				// Clear interval
				clearInterval(_i);
				thisView.setConfig(true);
			} else {
				_c++;
				if (_c === 10) {
					// Clear interval
					clearInterval(_i);
					// > Redirect
					thisView.setConfig(false);
				}
			}
		}, 200);

	},

	setConfig : function(shakeAllowed) {
		var thisView = this;
		if (shakeAllowed) {
			window.addEventListener("shake", function shakeEventOccured() {
				thisView.fnGetNextQuestion();
			}, false);
		} else {
			sap.m.MessageToast.show("Your browser does not support shake detection");
		}

		var configModel = this.getView().getModel("cfg");

		configModel.setProperty("/shakeSupported", shakeAllowed);
		configModel.setProperty("/shakeNotSupported", !shakeAllowed);
		configModel.setProperty("/showLoadingThingy", false);

		this.getView().byId("busyInd").destroy();
	},

	/**
	 * Similar to onAfterRendering, but this hook is invoked before the
	 * controller's View is re-rendered (NOT before the first rendering!
	 * onInit() is used for that one!).
	 * 
	 * @memberOf user.Question
	 */
	// onBeforeRendering: function() {
	//
	// },
	/**
	 * Called when the View has been rendered (so its HTML is part of the
	 * document). Post-rendering manipulations of the HTML could be done here.
	 * This hook is the same one that SAPUI5 controls get after being rendered.
	 * 
	 * @memberOf user.Question
	 */
	// onAfterRendering: function() {
	//
	// },
	/**
	 * Called when the Controller is destroyed. Use this one to free resources
	 * and finalize activities.
	 * 
	 * @memberOf user.Question
	 */
	// onExit: function() {
	//
	// }
	fnGetNextQuestion : function() {
		var thisView = this;
		$.ajax({
			url : getRemoteSystemUrl("/b/getQuestion"),
			type : "GET",
			xhrFields : {
				withCredentials : true
			},
			dataType : "json",
			async : true,
			success : loadNewQuestion,
			error : function(xhr, ajaxOptions, thrownError) {

				var msg = "";
				switch (xhr.status) {
				case 404:
					msg = "No new questions available try again soon!";
					break;

				default:
					msg = xhr.statusText;
					break;
				}

				sap.m.MessageToast.show(msg);
			}
		});

		function loadNewQuestion(data, textStatus, jqXHR) {

			var questionModel = thisView.getView().getModel("question");
			if (questionModel === undefined) {
				questionModel = new sap.ui.model.json.JSONModel({
					"dummy" : true
				});
				thisView.getView().setModel(questionModel, "question");
			}

			if (data.id == questionModel.getProperty("/id")) {
				var msg = "No new question found";
				sap.m.MessageToast.show(msg);
			} else {
				function formatCountdown() {
					var now = new Date();
					var secondsToExpire = Math.floor((questionModel.getProperty("/expireTime") - now) / 1000);

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

					questionModel.setProperty("/secondsRemaining", time);

					return secondsToExpire;
				}

				var msg = "Question retrieved";
				sap.m.MessageToast.show(msg);

				var t = new Date();
				t.setSeconds(t.getSeconds() + data.secondsRemaining);
				data.expireTime = t;
				questionModel.setData(data);
				formatCountdown();

				var configModel = thisView.getView().getModel("cfg");
				configModel.setProperty("/showQuestion", true);

				function decreaseTimer() {
					var secondsToExpire = formatCountdown();
					if (secondsToExpire <= 0) {
						var configModel = thisView.getView().getModel("cfg");
						configModel.setProperty("/showQuestion", false);
					} else {
						setTimeout(decreaseTimer, 1000);
					}
				}

				setTimeout(decreaseTimer, 1000);

			}
		}
	},
	fnVoteYes : function() {
		this.fnVote("YES");
	},
	fnVoteNo : function() {
		this.fnVote("NO");
	},
	fnVote : function(yesOrNo) {
		var thisView = this;
		var questionModel = this.getView().getModel("question");

		var responseData = {
			"questionId" : questionModel.getProperty("/id"),
			"response" : yesOrNo
		};
		$.ajax({
			url : questionModel.getProperty("/responseUrl"),
			type : "POST",
			xhrFields : {
				withCredentials : true
			},
			dataType : "json",
			data : JSON.stringify(responseData),
			async : true,
			success : handleVoteSubmitted,
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

		function handleVoteSubmitted() {
			sap.m.MessageToast.show("Your vote has been successfully submitted. Thank you!");
			var configModel = thisView.getView().getModel("cfg");
			configModel.setProperty("/showQuestion", false);
		}
	}
});