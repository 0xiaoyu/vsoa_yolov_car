package com.yu.utils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import java.util.Date;

public class VoiceUtil extends Thread {

    public ActiveXComponent sap = new ActiveXComponent("Sapi.SpVoice");
    public Dispatch sapo = sap.getObject();
    public Variant defalutVoice;
    public Dispatch dispdefaultVoice;
    public Variant allVoices;
    public Dispatch dispVoices;
    {
        // 音量 0-100
        sap.setProperty("Volume", new Variant(100));
        // 语音朗读速度 -10 到 +10
        sap.setProperty("Rate", new Variant(1.3));
        defalutVoice = sap.getProperty("Voice");
        dispdefaultVoice = defalutVoice.toDispatch();
        allVoices = Dispatch.call(sapo, "GetVoices");
        dispVoices = allVoices.toDispatch();
    }
    public void voice(String content, int type) {


        if (type == 0) {
            try {
                Dispatch setvoice = Dispatch.call(dispVoices, "Item",
                        new Variant(1)).toDispatch();
                ActiveXComponent voiceActivex = new ActiveXComponent(dispdefaultVoice);
                ActiveXComponent setvoiceActivex = new ActiveXComponent(setvoice);
                Variant item = Dispatch.call(setvoiceActivex, "GetDescription");

                // 执行朗读
                Dispatch.call(sapo, "Speak", new Variant(content));
                sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sapo.safeRelease();
                sap.safeRelease();
            }
        } else {
            // 停止
            try {
                Dispatch.call(sapo, "Speak", new Variant(content), new Variant(2));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public String content;
    public Long time = 0L;

    public void voice() {
        voice(content, 0);
    }


    public VoiceUtil(String content) {
        this.content = content;
    }

    @Override
    public void run() {
        long t = new Date().getTime();
        if (time + 10000 < t) {
            voice();
            time = t;
        }
    }
}
