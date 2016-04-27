package com.mind.httpclient.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.file.Files;
import java.security.KeyStore;

/**
 * Created by serv on 2014/8/22.
 */
public class SSLRestTemplateBuilder {


    /**
     * 允许的协议
     */
    private String[] supportedProtocols = new String[]{"TLSv1"};

    /**
     * 允许的密码套件
     */
    private String[] supportedCipherSuites = null;

    /**
     * 加密证书
     */
    private byte[] keyStoreBytes;

    /**
     * 加密秘钥
     */
    private String keyStorePassword;

    /**
     * 加密类型
     */
    private String keyStoreType = KeyStore.getDefaultType();

    /**
     * 信任密码
     */
    private String trustStorePassword;
    /**
     * 信任证书
     */
    private InputStream trustStoreInputStream;

    private TrustStrategy trustStrategy = new TrustSelfSignedStrategy();

    private RequestConfig config = RequestConfig.custom()
            .setSocketTimeout(5 * 1000)//超时 5秒
            .setConnectionRequestTimeout(5 * 1000)//
            .setConnectTimeout(5 * 1000)//超时 5秒
            .build();

    //Hostname校验, 例如: firefox 和 ie的区别 默认是 跟firefox的一致
    private X509HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

    public static SSLRestTemplateBuilder create() {
        return new SSLRestTemplateBuilder();
    }

    private SSLRestTemplateBuilder() {

    }


    /**
     * 设置信任策略
     *
     * @param trustStrategy
     * @return
     */
    public final SSLRestTemplateBuilder setTrustStrategy(final TrustStrategy trustStrategy) {
        this.trustStrategy = trustStrategy;
        return this;
    }

    /**
     * 设置加密证书
     *
     * @param keyStoreBytes
     * @return
     */
    public final SSLRestTemplateBuilder setKeyStore(final byte[] keyStoreBytes) {
        this.keyStoreBytes = keyStoreBytes;
        return this;
    }

    /**
     * 设置允许的协议
     *
     * @param supportedProtocols
     * @return
     */
    public final SSLRestTemplateBuilder setSupportedProtocols(final String[] supportedProtocols) {
        this.supportedProtocols = supportedProtocols;
        return this;
    }

    /**
     * 设置允许的密码套件
     *
     * @param supportedCipherSuites
     * @return
     */
    public final SSLRestTemplateBuilder setSupportedCipherSuites(final String[] supportedCipherSuites) {
        this.supportedCipherSuites = supportedCipherSuites;
        return this;
    }

    /**
     * 设置hostname校验器
     *
     * @param hostnameVerifier
     * @return
     */
    public final SSLRestTemplateBuilder setHostnameVerifier(final X509HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    /**
     * 设置加密秘钥
     *
     * @param keyStorePassword
     * @return
     */
    public final SSLRestTemplateBuilder setKeyStorePassword(final String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        return this;
    }


    /**
     * 设置加密证书类型
     *
     * @param keyStoreType
     */
    public final SSLRestTemplateBuilder setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
        return this;
    }

    /**
     * 设置信任秘钥
     *
     * @param trustStorePassword
     * @return
     */
    public final SSLRestTemplateBuilder setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
        return this;
    }

    /**
     * 设置信任证书
     *
     * @param trustStoreInputStream
     * @return
     */
    public final SSLRestTemplateBuilder setTrustStore(FileInputStream trustStoreInputStream) {
        this.trustStoreInputStream = trustStoreInputStream;
        return this;
    }

    /**
     * 设置信任证书
     *
     * @param trustStoreBytes
     * @return
     */
    public final SSLRestTemplateBuilder setTrustStore(final byte[] trustStoreBytes) {
        this.trustStoreInputStream = new ByteArrayInputStream(trustStoreBytes);
        return this;
    }

    public final SSLRestTemplateBuilder setConfig(RequestConfig config) {
        this.config = config;
        return this;
    }

    /**
     * 生成restTemplate对象
     * @return
     */
    public final HttpClient build(){
        try {
            KeyStore keyStore = null;
            if(keyStoreBytes!=null){
                keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(new ByteArrayInputStream(keyStoreBytes), keyStorePassword==null?null:keyStorePassword.toCharArray());
            }

            KeyStore trustStore = null;
            if(trustStoreInputStream!=null){
                trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(trustStoreInputStream, trustStorePassword==null?null:trustStorePassword.toCharArray());
            }

            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore,keyStorePassword==null?null:keyStorePassword.toCharArray())
                    .loadTrustMaterial(trustStore, trustStrategy)
                    .build();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    supportedProtocols,
                    supportedCipherSuites,
                    hostnameVerifier);
            return HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultRequestConfig(config).build();

        } catch (Exception e) {
            throw new RuntimeException("加载证书失败");
        }
    }


    public static void main(String[] args) throws IOException {
        HttpClient template = SSLRestTemplateBuilder.create()
//                .setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                .setKeyStoreType("PKCS12")
                .setKeyStore(Files.readAllBytes(new File("c:/client.p12").toPath()))
                .setKeyStorePassword("cx20091031")
                .setTrustStore(new FileInputStream("c:/truststore.jks"))
                .setTrustStorePassword("cx20091031")
                .build();


        String url = "https://www.ematong.com:8443/wxpay/getToken/sdf/sdjf";

        HttpUriRequest urlRequest = new HttpGet(url);

        HttpResponse execute = template.execute(urlRequest);

        System.out.println(execute.getEntity().toString());

        // 显示结果
        BufferedReader reader = new BufferedReader(new InputStreamReader(execute.getEntity().getContent(), "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

}
