package com.QuickHelp.AppUtils

class LocationNode {


    private var receiverid: String = ""
    private var receiverLat: String = ""
    private var receiverLng: String = ""
    private var senderid: String = ""
    private var senderLat: String = ""
    private var senderLng: String = ""
    private var time: String = ""


    fun getReceiverid(): String {
        return receiverid
    }

    fun setReceiverid(receiverid: String) {
        this.receiverid = receiverid
    }

    fun getReceiverLat(): String {
        return receiverLat
    }

    fun setReceiverLat(receiverLat: String) {
        this.receiverLat = receiverLat
    }

    fun getReceiverLng(): String {
        return receiverLng
    }

    fun setReceiverLng(receiverLng: String) {
        this.receiverLng = receiverLng
    }

    fun getSenderid(): String {
        return senderid
    }

    fun setSenderid(senderid: String) {
        this.senderid = senderid
    }

    fun getSenderLat(): String {
        return senderLat
    }

    fun setSenderLat(senderLat: String) {
        this.senderLat = senderLat
    }

    fun getSenderLng(): String {
        return senderLng
    }

    fun setSenderLng(senderLng: String) {
        this.senderLng = senderLng
    }

    fun getTime(): String {
        return time
    }

    fun setTime(time: String) {
        this.time = time
    }


    fun LocationNode() {

    }

    constructor() {

    }

    constructor(senderLat: String, senderLng: String) {

        this.senderLat = senderLat
        this.senderLng = senderLng


    }

    constructor(
        receiverid: String,
        receiverLat: String,
        receiverLng: String,
        senderid: String,
        senderLat: String,
        senderLng: String, timestamp: String
    ) {
        this.receiverid = receiverid
        this.receiverLat = receiverLat
        this.receiverLng = receiverLng

        this.senderid = senderid
        this.senderLat = senderLat
        this.senderLng = senderLng
        this.time = timestamp

    }


}