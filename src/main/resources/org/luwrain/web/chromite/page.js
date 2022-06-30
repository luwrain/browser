(function (){
    const enumChildren = (el)=>{
	const res = [];
	var e = el.firstElementChild;
	while (!!e) {
	    res.push({
		tagName: e.tagName,
		x: e.clientLeft,
		y: e.clientTop,
		width: e.clientWidth,
		height: e.clientHeight,
		children: enumChildren(e)});
	    e = e.nextElementSibling;
	}
	return res;
    };
    return JSON.stringify({children: enumChildren(document)});
})()
