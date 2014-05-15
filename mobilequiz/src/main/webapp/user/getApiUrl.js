/**
 * code to allow fetch of remote system URLs
 */

function getRemoteSystemUrl(relativeUrl) {
	// var remoteSystem =
	// "https://appsdiscoverypartner.hana.ondemand.com/mobilequiz";
	var remoteSystem = "http://wombling:8080/mobilequiz";
	return remoteSystem + relativeUrl;
}