(function (){
    const enumChildren = (el)=>{
	const res = [];
	var e = el.firstElementChild;
	while (!!e) {
	    res.push({tagName: e.tagName, children: enumChildren(e)});
	    e = e.nextElementSibling;
	}
	return res;
    };
    return JSON.stringify({children: enumChildren(document)});
})()
