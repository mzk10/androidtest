(function(){
	var list = window.imgscroll.options.img_list;
	var l="";
	var max = list.length;
	for (var int = 1; int <= max; int++) {
		l+=list[int-1].url;
		if (int<max) {
			l+=";";
		}
	}
	return l;
})()