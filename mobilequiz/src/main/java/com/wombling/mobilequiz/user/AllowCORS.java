package com.wombling.mobilequiz.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AllowCORS {

	public static void addCORSHeaders(HttpServletRequest request,
			HttpServletResponse response, String allowedVerbs) {
		String origin = request.getHeader("Origin");
		response.addHeader("Access-Control-Allow-Origin", origin);
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Allow-Methods", allowedVerbs
				+ ", OPTIONS");
		String requestCORSHeaders = request
				.getHeader("Access-Control-Request-Headers");
		if (requestCORSHeaders != null) {
			response.addHeader("Access-Control-Allow-Headers",
					requestCORSHeaders);
		}
	}
}
