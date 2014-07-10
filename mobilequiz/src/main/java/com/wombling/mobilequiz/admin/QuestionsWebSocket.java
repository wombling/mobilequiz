package com.wombling.mobilequiz.admin;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wombling.mobilequiz.api.ApiValues;
import com.wombling.mobilequiz.pojo.QuestionList;
import com.wombling.mobilequiz.user.QuestionService;

@ServerEndpoint(ApiValues.QUESTIONS_WEBSOCKET)
public class QuestionsWebSocket {

	Logger logger = LoggerFactory.getLogger(QuestionsWebSocket.class);

	private static Set<Session> clients = Collections
			.synchronizedSet(new HashSet<Session>());

	private static void sendCurrentValues(Session session, QuestionService qs)
			throws IOException {

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();

		QuestionList questionList = qs.getAllQuestions("");
		String questionsJSON = gson.toJson(questionList);

		session.getBasicRemote().sendText(questionsJSON);
	}

	public static void sendUpdate(QuestionService qs) {
		for (Session client : clients) {
			try {
				sendCurrentValues(client, qs);
			} catch (IOException e) {
				// ignore for now
			}
		}
	}

	@OnClose
	public void onClose(Session session) {
		// Remove session from the connected sessions set
		clients.remove(session);
	}

	@OnOpen
	public void onOpen(Session session) {
		// Add session to the connected sessions set
		clients.add(session);

		try {

			QuestionService qs = WebSocketSupportBean.getInstance().getQs();

			if (qs != null) {
				sendCurrentValues(session, qs);
			}
		} catch (IOException e) {
			// ignore for now
		}

	}

	@OnMessage
	public void processGreeting(String message, Session session) {
		logger.debug(message);
	}

}
