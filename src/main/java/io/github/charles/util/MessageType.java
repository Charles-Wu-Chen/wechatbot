package io.github.charles.util;


/*
Based on message type from
https://github.com/wechaty/java-wechaty/blob/master/wechaty-puppet/src/main/kotlin/io/github/wechaty/schemas/Message.kt

 */
public enum MessageType {
        Unknown(0),
        Attachment(1),  // Attach(6),
        Audio(2),  // Audio(1), Voice(34)
        Contact(3),  // ShareCard(42)
        ChatHistory(4),  // ChatHistory(19)
        Emoticon(5),  // Sticker: Emoticon(15), Emoticon(47)
        Image(6),  // Img(2), Image(3)
        Text(7),  // Text(1)
        Location(8),  // Location(48)
        MiniProgram(9),  // MiniProgram(33)
        Transfer(10),  // Transfers(2000)
        RedEnvelope(11),  // RedEnvelopes(2001)
        Recalled(12),  // Recalled(10002)
        Url(13),  // Url(5)
        Video(14);

        private final int code;

        MessageType(int code) {
                this.code = code;
        }

    }
