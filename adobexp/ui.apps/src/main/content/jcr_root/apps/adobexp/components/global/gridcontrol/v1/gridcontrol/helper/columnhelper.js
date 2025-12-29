use(function() {
	var value = this.layoutColumnsFieldName;
	var resourcePath = this.resourcePath;
	if (currentPage.getPath().indexOf("initial") != -1 && resource.getPath().indexOf("structure") != -1) {
		resourcePath = currentPage.getPath() + resource.getPath().substring(resource.getPath().indexOf("structure") + 9);
	}
	if (currentPage.getPath().indexOf("/content") != -1 && resource.getPath().indexOf("structure") != -1) {
		resourcePath = currentPage.getPath() + resource.getPath().substring(resource.getPath().indexOf("structure") + 9);
	}
	if (value == null) {
		value = "12";
	}
	var returnArray = [];
	var columnWidthArray = [];
	if (value.indexOf(',') != -1) {
		columnWidthArray = value.split(',');
	} else {
		columnWidthArray[columnWidthArray.length] = value;
	}

	var getResponsiveClassNames = function(desktopSize) {
		var className = "aem-GridColumn aem-GridColumn--default--" + desktopSize;
	
		switch(desktopSize) {
			case "8":
				className += " aem-GridColumn--tablet--12 aem-GridColumn--phone--12"
				break;
			case "6":
				className += " aem-GridColumn--tablet--12 aem-GridColumn--phone--12"
				break;
			case "4":
				className += " aem-GridColumn--tablet--5 aem-GridColumn--phone--12"
				break;
			case "3":
				className += " aem-GridColumn--tablet--6 aem-GridColumn--phone--6"
				break;
		}
		return className;
	}

	for (index in columnWidthArray) {
		var column = {
			columnClass : getResponsiveClassNames(columnWidthArray[index]),
			parSysName : resourcePath + "/column-" + index
		}
		index++;
		returnArray[returnArray.length] = column;
	}

	return returnArray;
});
