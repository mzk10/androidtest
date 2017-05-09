var adId, ssp, adFileName, apkUrl, responseType;
var targetId = '';
var clickId = '';
var displayTrackerUrl = '';
var clickTrackerUrl = '';
var adPrice = 0.00;
var param1 = "";
var param2 = "";
var param3 = "";
var param4 = "";
var adType = 2;

function downLoad_android() {
	InterstitialADInterface.close();
	/* alert(responseType); */
	/* 只上报点击，真正开始下载时，sdk上报开始下载 */
	eventType = 1;
	androidInterface.reportADEvent(adId, adType, eventType, ssp, clickId,
			targetId, displayTrackerUrl, clickTrackerUrl, adPrice, param1,
			param2, param3, param4);
	if (responseType == '0') {
		// alert(adId+"|"+adType+"|"+ssp+"|"+apkUrl+"|"+adFileName+"|"+clickId+"|"+targetId+"|"+displayTrackerUrl+"|"+clickTrackerUrl+"|"+adPrice+"|"+param1+"|"+param2+"|"+param3+"|"+param4);
		androidInterface.startDownload(adId, adType, ssp, apkUrl, adFileName,
				clickId, targetId, displayTrackerUrl, clickTrackerUrl, adPrice,
				param1, param2, param3, param4);
	} else {
		androidInterface.openBrowser(adId, adType, eventType, ssp, apkUrl,
				clickId, targetId, displayTrackerUrl, clickTrackerUrl, adPrice,
				param1, param2, param3, param4);
	}
}
function loadImage(url, callback) {
	var img = new Image(); // 创建一个Image对象，实现图片的预下载
	img.src = url;

	if (img.complete) { // 如果图片已经存在于浏览器缓存，直接调用回调函数
		callback.call(img);
		return; // 直接返回，不用再处理onload事件
	}
	img.onload = function() { // 图片下载完毕时异步调用callback函数。
		callback.call(img);// 将回调函数的this替换为Image对象
	};
};

function plaque_load_android(adId, fileName, resUrl, ssp, responseType,
		clickId, targetId, displayTrackerUrl, clickTrackerUrl, adPrice, param1,
		param2, param3, param4) {
	this.adId = adId;
	this.adFileName = fileName;
	this.apkUrl = resUrl;
	this.adType = 3;
	this.ssp = ssp;
	this.targetId = targetId;
	this.clickId = clickId;
	this.displayTrackerUrl = displayTrackerUrl;
	this.clickTrackerUrl = clickTrackerUrl;
	this.param1 = param1;
	this.param2 = param2;
	this.param3 = param3;
	this.param4 = param4;
	this.adPrice = adPrice;
	this.responseType = responseType;

}
// 露出上报
function reportAdEvent() {
	eventType = 0;
	androidInterface.reportADEvent(adId, adType, eventType, ssp, clickId,
			targetId, displayTrackerUrl, clickTrackerUrl, adPrice, param1,
			param2, param3, param4);
}