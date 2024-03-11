package cxyjy.it.fbga.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SSEServer {

  final static Logger log  = LoggerFactory.getLogger(SSEServer.class);

  private static AtomicInteger count = new AtomicInteger(0);

  private  Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

  SseEmitter sseEmitter = new SseEmitter();

  public  SseEmitter connect(String userId){
    //设置超时时间，0表示不过期，默认是30秒，超过时间未完成会抛出异常

    //注册回调
    sseEmitter.onCompletion(completionCallBack(userId));
    sseEmitter.onError(errorCallBack(userId));
    sseEmitter.onTimeout(timeOutCallBack(userId));
    sseEmitterMap.put(userId,sseEmitter);
    //数量+1
    count.getAndIncrement();
    log.info("create new sse connect ,current user:{}",userId);
    return sseEmitter;
  }

  public  void closeSseEmitter() {
    sseEmitter.onCompletion(()->{
      log.info("close");
      sseEmitter.getTimeout();
    });
  }


  /**
   * 给指定用户发消息
   */
  public void sendMessage(String userId, String message){
    if(sseEmitterMap.containsKey(userId)){
      try{
        sseEmitterMap.get(userId).send(message);
      }catch (IOException e){
        log.info("user id:{}, send message info:{}",userId,e.getMessage());
        e.printStackTrace();
      }
    }
  }

  /**
   * 想多人发送消息，组播
   */
  public  void groupSendMessage(String groupId, String message){
    if(sseEmitterMap!=null&&!sseEmitterMap.isEmpty()){
      sseEmitterMap.forEach((k,v) -> {
        try{
          if(k.startsWith(groupId)){
            v.send(message, MediaType.APPLICATION_JSON);
          }
        }catch (IOException e){
          log.info("user id:{}, send message info:{}",groupId,message);
          removeUser(k);
        }
      });
    }
  }
  public  void batchSendMessage(String message) {
    sseEmitterMap.forEach((k,v)->{
      try{
        v.send(message, MediaType.APPLICATION_JSON);
      }catch (IOException e){
        log.info("user id:{}, send message info:{}",k,e.getMessage());
        removeUser(k);
      }
    });
  }
  /**
   * 群发消息
   */
  public  void batchSendMessage(String message, Set<String> userIds){
    userIds.forEach(userId->sendMessage(userId,message));
  }

  public  void removeUser(String userId){
    sseEmitterMap.remove(userId);
    //数量-1
    count.getAndDecrement();
    log.info("remove user id:{}",userId);
  }
  public  List<String> getIds(){
    return new ArrayList<>(sseEmitterMap.keySet());
  }
  public  int getUserCount(){
    return count.intValue();
  }
  private  Runnable completionCallBack(String userId) {
    return () -> {
      log.info("结束连接,{}",userId);
      removeUser(userId);
//      sseEmitter.complete();
    };
  }
  private  Runnable timeOutCallBack(String userId){
    return ()->{
      log.info("连接超时,{}",userId);
      removeUser(userId);
    };
  }
  private  Consumer<Throwable> errorCallBack(String userId) {
    return throwable -> {
      log.error("连接异常，{}", userId);
      removeUser(userId);
    };
  }

}
