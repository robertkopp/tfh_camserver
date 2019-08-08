/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.services;

import dev.robertkopp.autocam.model.ClientRaspi;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author robert kopp
 */
public class TCPConnector {

    LinkedList<Channel> channels = new LinkedList<>();
    EventLoopGroup workerGroup;
    private List<ClientRaspi> raspis;

    public TCPConnector() {

    }

    public void setClients(List<ClientRaspi> raspis) {
        this.raspis = raspis;
    }

    public void setClient(ClientRaspi raspi) {
        raspis = new LinkedList<>();
        raspis.add(raspi);
    }

    public void openChannels() {
        workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {

                    ch.pipeline().addLast("encoder", new MessageEncoder());
                }
            });

            for (ClientRaspi client : raspis) {
                // Start the client.
                ChannelFuture f = b.connect(client.getIpAdress(), 9999).syncUninterruptibly(); // (5)
                Channel ch = f.awaitUninterruptibly().channel();
                channels.add(ch);

            }
            // Wait until the connection is closed.

        } finally {

        }
    }

    public void sendTakeSingleShot(String camId) {
        Message m = new Message(Commands.TakeSingleShot, camId);
        try {
            for (Channel ch : channels) {
                ch.writeAndFlush(m);

            }

        } finally {

        }
    }
    
    public void sendConfig(String json) {
        Message m = new Message(Commands.SendConfig, json);
        try {
            for (Channel ch : channels) {
                ch.writeAndFlush(m);

            }

        } finally {

        }
    }
    
    public void sendPing() {
        Message m = new Message(Commands.Ping, null);
        try {
            for (Channel ch : channels) {
                ch.writeAndFlush(m);

            }

        } finally {

        }
    }

    void engage(String shotId) {
        byte cmd = Commands.TakeShot;
        Message m = new Message(cmd,shotId);
        try {
            for (Channel ch : channels) {
                ch.writeAndFlush(m);

            }

        } finally {

        }
    }

    public void shutDown() {
        for (Channel ch : channels) {
            ch.close();

        }
        channels.clear();
        workerGroup.shutdownGracefully();

    }

}
