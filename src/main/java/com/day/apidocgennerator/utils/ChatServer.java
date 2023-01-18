package com.day.apidocgennerator.utils;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.day.apidocgennerator.actions.RightClickMenuAction;
import com.day.apidocgennerator.ui.ApiDocGenerator;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;


/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */

public class ChatServer extends WebSocketServer {

  public int port;

  public ChatServer(int port) throws UnknownHostException {
    super(new InetSocketAddress(port));
  }

  public ChatServer(InetSocketAddress address) {
    super(address);
  }

  public ChatServer(int port, Draft_6455 draft) {
    super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    if(ApiDocGenerator.methodVoList==null||ApiDocGenerator.methodVoList.isEmpty())return;  //TODO EMPTY
    JSONObject json = new JSONObject();
    json.set("methods",ApiDocGenerator.methodVoList.values());
    json.set("models",ApiDocGenerator.modelList);
    conn.send(json.toJSONString(0));
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    GenerateCodeService service = new GenerateCodeService(JSONUtil.parseObj(message), RightClickMenuAction.packagePath, RightClickMenuAction.CurFile);
    service.Start();
//    System.out.println(message);
  }

  @Override
  public void onMessage(WebSocket conn, ByteBuffer message) {
  }


  public static ChatServer startServer(int port) {
    ChatServer s;
    try {
       s = new ChatServer(port);
      s.start();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
    s.port = port;
    return s;
  }
  public static void stopServer(ChatServer s) {
    if(s==null) return;
    try {
      s.stop();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    ex.printStackTrace();
    if (conn != null) {
      // some errors like port binding failed may not be assignable to a specific websocket
    }
  }

  @Override
  public void onStart() {
    System.out.println("Server started!");
    setConnectionLostTimeout(0);
    setConnectionLostTimeout(100);
  }
}