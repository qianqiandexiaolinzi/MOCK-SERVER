package com.zouq.controller;

import com.zouq.entity.MockApi;
import com.zouq.entity.MockService;
import com.zouq.repository.MockServiceRepository;
import com.zouq.repository.MockApiRepository;
import com.zouq.service.RequestForwardingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ForwardingController {

    @Autowired
    private MockServiceRepository serviceRepo;

    @Autowired
    private MockApiRepository apiRepo;

    @Autowired
    private RequestForwardingService forwardingService;

    /**
    *捕获所有请求：/{serviceName}/任意路径
    * method=支持所有http方法
    * */
    @RequestMapping(value = "/{serviceName}/**",method={
        RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.PATCH,RequestMethod.DELETE,RequestMethod.OPTIONS
    })
    public ResponseEntity<?> handleRequest(
            @PathVariable String serviceName,
            HttpServletRequest request,
            @RequestBody(required = false) String requestBody
    ){
        System.out.printf("[Mock Server] Received %s request: /%s%s%n",
                request.getMethod(),serviceName,request.getRequestURI());
        //1.根据serviceName查询服务配置
        Optional<MockService> serviceOpt=serviceRepo.findByServiceNameIgnoreCase(serviceName);
        if (serviceOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("服务名称不存在"+serviceName);
        }
        MockService service=serviceOpt.get();

        if(!Boolean.TRUE.equals(service.isEnabled()))   {
            return ResponseEntity.status(503).body("服务不可用"+serviceName);
        }
        //2.提取接口路径，去掉serviceName
        String fullUri = request.getRequestURI();
        String pathWithinService = extractPathAfterService(fullUri,serviceName);
        if (pathWithinService == null) {
            return ResponseEntity.badRequest().body("获取接口路径异常");
        }
        //3.获取http方法
        String httpMethod=request.getMethod();

        //4.查询api配置
        Optional<MockApi> apiOpt = apiRepo.findByServiceAndApiPathAndMethod(
                service,pathWithinService,httpMethod
        );
        //如果配了服务，没有配置接口，则真实转发。
        if(apiOpt.isEmpty()){
            System.out.printf("接口没配置，直接转发");
            //return ResponseEntity.badRequest().body("接口未配置" + pathWithinService +"{"+httpMethod+"]");
            return forwardingService.forward(service.getRealUrl(), pathWithinService, request, requestBody);
        }
        MockApi api=apiOpt.get();

        //5.判断是否启用mock
        if (Boolean.TRUE.equals(api.getIsMockEnable())) {
            try {
                Thread.sleep(api.getDelayMs());  //模拟延迟
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
            return ResponseEntity.status(api.getStatusCode()).body(api.getResponseBody()); //直接返回json字符串
        }else  {
            return forwardingService.forward(service.getRealUrl(), pathWithinService, request, requestBody);
        }
    }

    //从完整uri中提取/serviceName之后的部分
    private String extractPathAfterService(String fullUri,String serviceName){
        String prefix = "/" + serviceName;
        if (!fullUri.startsWith(prefix)) {
            return null;
        }
        String path = fullUri.substring(prefix.length());

        if (path.isEmpty()) {
            return "/";
        }
        if (path.startsWith("/")) {
            path = path;
        }
        return path;
    }
    //简单转义json字符串中的双引号
    private String escapeJson(String str){
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
