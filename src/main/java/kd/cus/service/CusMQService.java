//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package kd.cus.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.*;
import kd.bos.context.RequestContext;
import kd.bos.context.RequestContextCreator;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.framework.lifecycle.Service;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.mq.MQFactory;
import kd.bos.mq.MessagePublisher;
import kd.bos.servicehelper.permission.PermissionServiceHelper;
import kd.cus.action.MQMainTemplate;
import kd.cus.api.PUBConfigUtils.SpicRabbitMQConfig;
import kd.cus.entity.MQMessage;
import kd.cus.entity.OperateEnmu;
import kd.cus.utils.LogBillUtils;
import kd.cus.utils.LogEntity;
import kd.cus.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;



public class CusMQService implements Service {

    boolean started;

    public CusMQService() {
    }

    public String getName() {
        return "DemoMQService";
    }
//
    public boolean isStarted() {
        return this.started;
    }


    public void start() {
        try {
            getMQPower();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getMQPower(){
        Log logger = LogFactory.getLog(CusMQService.class);
        SpicRabbitMQConfig config = SpicRabbitMQConfig.getSpicRabbitMQConfig();
        logger.info("" +
                "-------------------------------------MQLOG--初始化------------------------------------------------\r\n" +
                "RabbitMQHost:\t" + config.getRabbitMQHost() + "\r\n" +
                "RabbitMQPort:\t" + config.getRabbitMQPort() + "\r\n" +
                "Username:\t" + config.getUsername() + "\r\n" +
                "Password:\t" + config.getPassword() + "\r\n" +
                "VirtualHost:\t" + config.getVirtualHost() + "\r\n" +
                "Exchange:\t" + config.getExchange() + "\r\n" +
                "ExchangeType:\t" + config.getExchangeType() + "\r\n" +
                "QueneName:\t" + config.getQueneName() + "\r\n"  +
                "SystemCode:\t" + config.getSystemCode() + "\r\n"  +
                "-------------------------------------MQLOG--初始化------------------------------------------------\r\n");
//        LogUtils.log(null, "secmq", "二开rabbitMQ", "MQ系统上线", "MQ系统上线", new Date(),null);
        String queneName = config.getQueneName();
        Connection connection = null;
        try {
            //本地测试
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(config.getRabbitMQHost());
            factory.setPort(Integer.parseInt(config.getRabbitMQPort()));
            factory.setUsername(config.getUsername());
            factory.setPassword(config.getPassword());
            factory.setVirtualHost(config.getVirtualHost());
            connection = factory.newConnection();
            /**/
            //苍穹测试
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(config.getExchange(),config.getExchangeType());
            channel.queueDeclare(queneName,true, false, false, new HashMap<>());
            channel.basicConsume(queneName, false, "", new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
//                    LogUtils.log(null, "secmq", "二开rabbitMQ", "请求开始", "请求开始", new Date(),null);
                    String message = new String(body, "UTF-8");
//                    LogEntity start = LogBillUtils.createLog(message, null, "4A", "4A", "4a权限");
                    System.out.println(envelope.getExchange() + "," + envelope.getRoutingKey() + ":");
                    System.out.println(message);
//                    channel.basicConsume(queneName, false, consumer);
                    MQMessage mqMessage = JSON.parseObject(message, MQMessage.class);
//                    disposeMessage(mqMessage);
                    if (config.getSystemCode().equals(mqMessage.getSysCode())) {
                        channel.basicAck(envelope.getDeliveryTag(), false); //
                        MQMainTemplate template = MQTypeFactory.getType(mqMessage.getType());
                        try {
                            String configs = System.getProperty("WS_CONFIG");
                            System.setProperty("bos.checkThreadContext", "false");
                            if (StringUtils.isNotBlank(configs)) {
                                RequestContextCreator.createForTripSI(configs.split(",")[0], configs.split(",")[1], configs.split(",")[2]);
                            }
//                            PermissionServiceHelper.newRole()
                            String result = template.dispose(OperateEnmu.getOperate(mqMessage.getOperation()), mqMessage.getBody());
//                            LogUtils.log(true, "secmq", "二开rabbitMQ", message, result, new Date(),null);
//                            LogBillUtils.modifyLog(start,"1", result,"4A");
                            channel.basicPublish(config.getExchange(),"",null,mqMessage.getMsgId().getBytes("UTF-8"));
                            if(StringUtils.isNotBlank(result)){
                                logger.info("" +
                                        "-------------------------------------MQLOG--失败------------------------------------------------\r\n" +
                                        "消息" + result+"\r\n"+
                                        "" +mqMessage.toString() +
                                        "-------------------------------------MQLOG--------------------------------------------------\r\n");
                            }else {
                                logger.info("" +
                                        "-------------------------------------MQLOG--成功------------------------------------------------\r\n" +
                                        "" +mqMessage.toString() +
                                        "-------------------------------------MQLOG--------------------------------------------------\r\n");
                            }
                        }catch (Exception e){
//                            LogUtils.log(false, "secmq", "二开rabbitMQ", message, "", new Date(),e);
//                            LogBillUtils.modifyLog(start,"2", "","4A");
//                            e.printStackTrace();
                            logger.info("" +
                                    "-------------------------------------MQLOG--失败------------------------------------------------\r\n" +
                                    "e" + e.getMessage()+"\r\n"+
                                    "" +mqMessage.toString() +
                                    "-------------------------------------MQLOG--------------------------------------------------\r\n");
                        }

                    }
                }
            });
            // channel绑定队列，autoAck为true表示一旦收到消息则自动回复确认消息

        } catch (Exception ex) {
//            LogUtils.log(false, "secmq", "二开rabbitMQ", "", "", new Date(),ex);
//            LogBillUtils.modifyLog(null,"2", "","4A");
//            ex.printStackTrace();
        }
        return "";
    }

    public void stop() {
    }

    public String disposeMessage(MQMessage mqMessage){
        return null;
    }
}
