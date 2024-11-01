package com.example.macro.grpc

import android.util.Log
import com.example.macro.macro.KeyEvent
import com.example.macro.macro.KeyboardMacro

class InputService(private val keyboardMacro: KeyboardMacro): GrpcService() {

    init {
        nativeObj = nativeCreateObject(50051)
        Log.d(TAG, "input service pointer $nativeObj")
    }

    private fun startRequest(fileName: String) {
        Log.d(TAG, "start request $fileName")
        keyboardMacro.startRecord(fileName)
    }

    private fun stopRequest() {
        Log.d(TAG, "stop request")
        keyboardMacro.stopRecord()
    }

    private fun startReplayDebug(writerPtr: Long, fileName: String) {
        // 파일에서 KeyEvent 목록을 로드
        val events = keyboardMacro.loadRecordedFile(fileName)

        // replayRecordFile을 호출하고 각 이벤트에 대해 sendReplayKeyEvent를 호출
        keyboardMacro.replayRecordFile(events) { event ->
            // 이벤트의 설명 문자열을 생성
            val eventDescription = "Event at ${event.delay}ns"

            // gRPC를 통해 KeyEvent 전송
            sendReplayKeyEvent(writerPtr, eventDescription)
        }
    }

    private fun getMacroDetail(fileName: String): List<KeyEvent> {
        var events = keyboardMacro.loadRecordedFile(fileName);

        return events;
    }

    private fun getFileList(): List<String> {
        return keyboardMacro.getFileList();
    }

    private external fun nativeCreateObject(port: Int): Long
    private external fun nativeDestroyObject(nativeObj: Long)
    private external fun sendReplayKeyEvent(writerPtr: Long, eventDescription: String)
    companion object {
        var TAG = "InputService";
        init {
            System.loadLibrary("native_lib")
        }
    }
}