/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

// For use with Commons-HTTPClient
class UntrustedSSLProtocolSocketFactory
    extends SSLProtocolSocketFactory
{
    private SSLSocketFactory factory;

    private static Protocol defaultSSL;
    private static Protocol untrustSSL;

    private static boolean isRegistered() {
        Protocol https = Protocol.getProtocol("https");
        boolean isRegistered =
            https.getSocketFactory() instanceof UntrustedSSLProtocolSocketFactory;
        if (!isRegistered) {
            defaultSSL = https;
        }
        return isRegistered;
    }

    public static void register() {
        //don't croak on self-signed certs

        if (!isRegistered()) {
            if (untrustSSL == null) {
                ProtocolSocketFactory factory =
                    (ProtocolSocketFactory)new UntrustedSSLProtocolSocketFactory();
                untrustSSL =
                    new Protocol("https", factory, 443);
            }
            Protocol.registerProtocol("https", untrustSSL);
        }
    }

    public static void unregister() {
        if (isRegistered()) {
            Protocol.registerProtocol("https", defaultSSL);
        }
    }

    public UntrustedSSLProtocolSocketFactory() {
        super();

        try {
            BogusTrustManager trustMan;
            SSLContext tlsContext;

            trustMan = new BogusTrustManager();
            tlsContext = SSLContext.getInstance("TLS");
            tlsContext.init(null, new X509TrustManager[] { trustMan },
                            null);
            this.factory = tlsContext.getSocketFactory();
        } catch(NoSuchAlgorithmException exc){
            throw new IllegalStateException("Unable to get SSL context: "+
                                            exc.getMessage());
        } catch(KeyManagementException exc){
            throw new IllegalStateException("Unable to initialize ctx " +
                                            "with BogusTrustManager: " +
                                            exc.getMessage());
        }
    }

    public Socket createSocket(String host, int port,
                               InetAddress clientHost, int clientPort)
        throws IOException
    {
        return this.factory.createSocket(host, port, clientHost,
                                         clientPort);
    }

    public Socket createSocket(String host, int port)
        throws IOException
    {
        return this.factory.createSocket(host, port);
    }

    public Socket createSocket(Socket socket, String host, int port,
                               boolean autoClose)
        throws IOException
    {
        return this.factory.createSocket(socket, host, port, autoClose);
    }

    public Socket createSocket(String host, int port,
                               InetAddress clientHost,
                               int clientPort,
                               HttpConnectionParams params)
        throws IOException
    {
        //as of 3.0 super.createSocket jumps through some hoops to support
        //timeout in jre 1.3 and in the process by-passes our factory
        return createSocket(host, port, clientHost, clientPort);
    }

    static final class BogusTrustManager
        implements X509TrustManager
    {
        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) {}

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) {}

        //required for jdk 1.3/jsse 1.0.3_01
        public boolean isClientTrusted(X509Certificate[] chain) {
            return true;
        }

        //required for jdk 1.3/jsse 1.0.3_01
        public boolean isServerTrusted(X509Certificate[] chain) {
            return true;
        }

        public X509Certificate[] getAcceptedIssuers(){
            return null;
        }
    }
}



