package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.jrebelutil.JrebelSign;
import com.example.demo.util.rsasign;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
class CustomersHttpController {

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return Set.of(new Customer(1, "A"), new Customer(2, "B"), new Customer(3, "C"));
    }

    @RequestMapping("/")
    void index(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // 拼接服务器地址
        String licenseUrl =
                request.getScheme()
                        + "://"
                        + request.getServerName()
                        + ":"
                        + request.getServerPort();

        StringBuffer html = new StringBuffer("<h3>使用说明（Instructions for use）</h3>");

        html.append("<hr/>");

        html.append("<h1>Hello,This is a Jrebel & JetBrains License Server!</h1>");
        html.append("<p>License Server started at ").append(licenseUrl);
        html.append("<p>JetBrains Activation address was: <span style='color:red'>")
                .append(licenseUrl)
                .append("/");
        html.append(
                        "<p>JRebel 7.1 and earlier version Activation address was: <span style='color:red'>")
                .append(licenseUrl)
                .append("/{tokenname}")
                .append("</span>, with any email.");
        html.append("<p>JRebel 2018.1 and later version Activation address was: ")
                .append(licenseUrl)
                .append("/{guid}")
                .append("(eg:<span style='color:red'>")
                .append(licenseUrl)
                .append("/")
                .append(UUID.randomUUID())
                .append("</span>), with any email.");

        html.append("<hr/>");

        html.append("<h1>Hello，此地址是 Jrebel & JetBrains License Server!</h1>");
        html.append("<p>JetBrains许可服务器激活地址 ").append(licenseUrl);
        html.append("<p>JetBrains激活地址是: <span style='color:red'>").append(licenseUrl).append("/");
        html.append("<p>JRebel 7.1 及旧版本激活地址: <span style='color:red'>")
                .append(licenseUrl)
                .append("/{tokenname}")
                .append("</span>, 以及任意邮箱地址。");
        html.append("<p>JRebel 2018.1+ 版本激活地址: ")
                .append(licenseUrl)
                .append("/{guid}")
                .append("(例如：<span style='color:red'>")
                .append(licenseUrl)
                .append("/")
                .append(UUID.randomUUID())
                .append("</span>), 以及任意邮箱地址。");

        response.getWriter().println(html);
    }

    @RequestMapping("/jrebel/leases")
    void jrebelLeasesHandler(HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        logRequest(request);
        jrebelLeasesHandler0(response, request);
    }



    @RequestMapping("/jrebel/leases/1")
    void jrebelLeases1Handler(HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        logRequest(request);
        jrebelLeasesHandler1(response, request);
    }





    @RequestMapping("/agent/leases")
    void agentLeasesHandler(HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        logRequest(request);
        jrebelLeasesHandler0(response, request);
    }

    @RequestMapping("/agent/leases/1")
    void agentLeases1Handler(HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        logRequest(request);
        jrebelLeasesHandler1(response, request);
    }


    @RequestMapping("jrebel/validate-connection")
    void jrebelValidateHandler(HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        logRequest(request);
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String jsonStr =
                """
                    {
                        "serverVersion": "3.2.4",
                        "serverProtocolVersion": "1.1",
                        "serverGuid": "a1b4aea8-b031-4302-b602-670a990272cb",
                        "groupType": "managed",
                        "statusCode": "SUCCESS",
                        "company": "Administrator",
                        "canGetLease": true,
                        "licenseType": 1,
                        "evaluationLicense": false,
                        "seatPoolType": "standalone"
                    }
                    """;
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String body = jsonObject.toString();
        response.getWriter().print(body);
    }

    @RequestMapping("/rpc/ping.action")
    void pingHandler(HttpServletResponse response, HttpServletRequest request) throws IOException {
        logRequest(request);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String salt = request.getParameter("salt");
        if (salt == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            String xmlContent =
                    "<PingResponse><message></message><responseCode>OK</responseCode><salt>"
                            + salt
                            + "</salt></PingResponse>";
            String xmlSignature = rsasign.Sign(xmlContent);
            String body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
            response.getWriter().print(body);
        }
    }

    @RequestMapping("/rpc/obtainTicket.action")
    void obtainTicketHandler(HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        logRequest(request);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        // response.setHeader("Date", date);
        // response.setHeader("Server", "fasthttp");
        String salt = request.getParameter("salt");
        String username = request.getParameter("userName");
        String prolongationPeriod = "607875500";
        if (salt == null || username == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            String xmlContent =
                    "<ObtainTicketResponse><message></message><prolongationPeriod>"
                            + prolongationPeriod
                            + "</prolongationPeriod><responseCode>OK</responseCode><salt>"
                            + salt
                            + "</salt><ticketId>1</ticketId><ticketProperties>licensee="
                            + username
                            + "\tlicenseType=0\t</ticketProperties></ObtainTicketResponse>";
            String xmlSignature = rsasign.Sign(xmlContent);
            String body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
            response.getWriter().print(body);
        }
    }

    @RequestMapping("/rpc/releaseTicket.action")
    void releaseTicketHandler(HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        logRequest(request);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String salt = request.getParameter("salt");
        if (salt == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            String xmlContent =
                    "<ReleaseTicketResponse><message></message><responseCode>OK</responseCode><salt>"
                            + salt
                            + "</salt></ReleaseTicketResponse>";
            String xmlSignature = rsasign.Sign(xmlContent);
            String body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
            response.getWriter().print(body);
        }
    }


    private static void logRequest(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        JSONObject paramMap = new JSONObject();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            paramMap.put(parameterName, request.getParameter(parameterName));
        }
        String urlParam = paramMap.entrySet().stream().map(x -> x.getKey() + "=" + x.getValue()).collect(Collectors.joining("&"));
        log.info(
                "uri:【{}】,params:【{}】",
                request.getRequestURI(),
                urlParam);
    }

    void jrebelLeasesHandler0(HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String clientRandomness = request.getParameter("randomness");
        String username = request.getParameter("username");
        String guid = request.getParameter("guid");
        boolean offline = Boolean.parseBoolean(request.getParameter("offline"));
        String validFrom = "null";
        String validUntil = "null";
        if (offline) {
            String clientTime = request.getParameter("clientTime");
            String offlineDays = request.getParameter("offlineDays");
            // long clinetTimeUntil = Long.parseLong(clientTime) + Long.parseLong(offlineDays)
            // * 24 * 60 * 60 * 1000;
            long clinetTimeUntil = Long.parseLong(clientTime) + 180L * 24 * 60 * 60 * 1000;
            validFrom = clientTime;
            validUntil = String.valueOf(clinetTimeUntil);
        }
        String jsonStr =
                """
                    {
                        "serverVersion": "3.2.4",
                        "serverProtocolVersion": "1.1",
                        "serverGuid": "a1b4aea8-b031-4302-b602-670a990272cb",
                        "groupType": "managed",
                        "id": 1,
                        "licenseType": 1,
                        "evaluationLicense": false,
                        "signature": "OJE9wGg2xncSb+VgnYT+9HGCFaLOk28tneMFhCbpVMKoC/Iq4LuaDKPirBjG4o394/UjCDGgTBpIrzcXNPdVxVr8PnQzpy7ZSToGO8wv/KIWZT9/ba7bDbA8/RZ4B37YkCeXhjaixpmoyz/CIZMnei4q7oWR7DYUOlOcEWDQhiY=",
                        "serverRandomness": "H2ulzLlh7E0=",
                        "seatPoolType": "standalone",
                        "statusCode": "SUCCESS",
                        "offline": %s,
                        "validFrom": %s,
                        "validUntil": %s,
                        "company": "Administrator",
                        "orderId": "",
                        "zeroIds": [
                           \s
                        ],
                        "licenseValidFrom": 1490544001000,
                        "licenseValidUntil": 1691839999000
                    }"""
                        .formatted(String.valueOf(offline), validFrom, validUntil);

        JSONObject jsonObject = JSON.parseObject(jsonStr);
        if (clientRandomness == null || username == null || guid == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            JrebelSign jrebelSign = new JrebelSign();
            jrebelSign.toLeaseCreateJson(clientRandomness, guid, offline, validFrom, validUntil);
            String signature = jrebelSign.getSignature();
            jsonObject.put("signature", signature);
            jsonObject.put("company", username);
            String body = jsonObject.toString();
            response.getWriter().print(body);
        }
    }


    private static void jrebelLeasesHandler1(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
        String username = request.getParameter("username");
        String jsonStr =
                "{\n"
                        + "    \"serverVersion\": \"3.2.4\",\n"
                        + "    \"serverProtocolVersion\": \"1.1\",\n"
                        + "    \"serverGuid\": \"a1b4aea8-b031-4302-b602-670a990272cb\",\n"
                        + "    \"groupType\": \"managed\",\n"
                        + "    \"statusCode\": \"SUCCESS\",\n"
                        + "    \"msg\": null,\n"
                        + "    \"statusMessage\": null\n"
                        + "}\n";
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        if (username != null) {
            jsonObject.put("company", username);
        }
        String body = jsonObject.toString();
        response.getWriter().print(body);
    }

    record Customer(Integer id, String name) {}
}
