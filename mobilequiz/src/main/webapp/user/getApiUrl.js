/**
 * code to allow fetch of remote system URLs
 */

function getRemoteSystemUrl(relativeUrl) {
	// var remoteSystem =
	// "https://yourHCPaccountHere.hana.ondemand.com/mobilequiz";
	var remoteSystem = window.location.href.substring(0,window.location.href.lastIndexOf("/"));
	return remoteSystem + relativeUrl;
}