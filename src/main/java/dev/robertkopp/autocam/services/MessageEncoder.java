/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.services;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * @author robert kopp
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {
    
    @Override    
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) {
        out.writeByte(msg.cmd);
        if (msg.payload!=null){
            byte[] data= msg.payload.getBytes();
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
