
window.locationService = {
	execute: function(action, successCallback, errorCallback) {
		cordova.exec(
			function(pos) {
				console.log(JSON.stringify(pos));
				successCallback(pos);
			},
			function(err){
				console.err(err);
				errorCallback(err);
			},
			"AmapLocation",
			action,
			[]
		)
	},
	getCurrentPosition: function(successCallback, errorCallback) {
		this.execute("getCurrentPosition", successCallback, errorCallback);
	},
	stop: function(successCallback, errorCallback) {
		this.execute("stop", successCallback, errorCallback);
	}
}
module.exports = locationService;

