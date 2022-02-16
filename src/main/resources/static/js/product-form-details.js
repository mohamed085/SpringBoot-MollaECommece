$(document).ready(function() {
	$("a[name='linkRemoveDetail']").each(function(index) {
		$(this).click(function() {
			removeDetailSectionByIndex(index);
		});
	});

});


function addNextDetailSection() {
	allDivDetails = $("[id^='divDetail']"); /** first id="divDetail0" defined in product_details.html then id="divDetail1" id="divDetail2" ... */
	divDetailsCount = allDivDetails.length;

	htmlDetailSection = `
		<div class="form-inline d-flex mb-3" id="divDetail${divDetailsCount}">
		    <input type="hidden" name="detailIDs" value="0" />
			<label class="me-3 ms-3 mt-auto mb-auto">Name:</label>
			<input type="text" class="form-control" name="detailNames" maxlength="255" />
			<label class="me-3 ms-3 mt-auto mb-auto">Value:</label>
			<input type="text" class="form-control" name="detailValues" maxlength="255" />
		</div>	
	`;

	$("#divProductDetails").append(htmlDetailSection);

	previousDivDetailSection = allDivDetails.last(); // get first previous value of the last element -> For instance : there are 2 elements,
	// it only shows the icon at first one, like 3 elements , it shows the icon at 2th element.

	previousDivDetailID = previousDivDetailSection.attr("id");

	htmlLinkRemove = `
		<a class="btn fas fa-times-circle icon-dark me-3 mb-3 mt-auto mb-auto"
			href="javascript:removeDetailSectionById('${previousDivDetailID}')"
			title="Remove this detail"></a>
	`;

	previousDivDetailSection.append(htmlLinkRemove);

	$("input[name='detailNames']").last().focus();
}

function removeDetailSectionById(id) {
	$("#" + id).remove();
}

function removeDetailSectionByIndex(index) {
	$("#divDetail" + index).remove();
} 