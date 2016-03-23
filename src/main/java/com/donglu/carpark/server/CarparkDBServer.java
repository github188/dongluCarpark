package com.donglu.carpark.server;

import org.eclipse.jetty.servlet.ServletHandler;

import com.donglu.carpark.server.servlet.CarparkServlet;
import com.donglu.carpark.server.servlet.ImageUploadServlet;
import com.donglu.carpark.server.servlet.InOutServlet;
import com.donglu.carpark.server.servlet.PlateSubmitServlet;
import com.donglu.carpark.server.servlet.ServerServlet;
import com.donglu.carpark.server.servlet.StoreServiceServlet;
import com.donglu.carpark.server.servlet.StoreServlet;
import com.donglu.carpark.server.servlet.UserServlet;
import com.dongluhitec.card.server.ServerUtil;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class CarparkDBServer {
	@Inject
	private Provider<UserServlet> userServlerProvider;
	@Inject
	private Provider<CarparkServlet> carparkServletProvider;
	@Inject
	private Provider<InOutServlet> inOutServletProvider;
	@Inject
	private Provider<ImageUploadServlet> imageServletProvider;
	@Inject
	private Provider<StoreServlet> storeServletProvider;
	@Inject
	private Provider<ServerServlet> serverServletProvider;
	@Inject
	private Provider<StoreServiceServlet> storeServiceServletProvider;
	@Inject
	private Provider<PlateSubmitServlet> plateSubmitServletProvider;
	
	public void startDbServlet(ServletHandler handler){
		ServerUtil.startServlet("/user/*", handler, userServlerProvider);
		ServerUtil.startServlet("/carpark/*", handler, carparkServletProvider);
		ServerUtil.startServlet("/inout/*", handler, inOutServletProvider);
		ServerUtil.startServlet("/storeservice/*", handler, storeServiceServletProvider);
		ServerUtil.startServlet("/carparkImage/*", handler, imageServletProvider);
		ServerUtil.startServlet("/store/*", handler, storeServletProvider);
		ServerUtil.startServlet("/server/*", handler, serverServletProvider);
		ServerUtil.startServlet("/plateSubmit/*", handler, plateSubmitServletProvider);
	}
}