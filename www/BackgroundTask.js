var exec = require('cordova/exec');

var backgroundTask = {
    addUser: function(url, userId, version, successCallback, errorCallback, frequency) {

        if(typeof url != 'string'){
            console.error('url is required');
            return;
        }

        if(typeof userId != 'string'){
            console.error('userId is required');
            return;
        }

        if(typeof version != 'string'){
            console.error('version is required');
            return;
        }

        if(typeof frequency == 'undefined'){
            frequency = 0;
        }

        if(typeof errorCallback == 'undefined'){
            errorCallback = function(e){
                console.log('backgroundTask');
                console.log('errorCallback');
                console.log(e);
            };
        }

        if(typeof successCallback == 'undefined'){
            successCallback = function(result){
                console.log('backgroundTask');
                console.log('successCallback');
                console.log(result);
            };
        }

        exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'BackgroundTask', // mapped to our native Java class called "BackgroundTask"
            'add', // with this action name
            [{                  // and this array of custom arguments to create our entry
                "frequency": frequency,
                "url": url,
                "user": userId,
                "version": version,
            }]
        );
     },
     setEnabled: function(isEnabled, successCallback, errorCallback, frequency) {

        if(typeof isEnabled == 'undefined'){
            console.error('isEnabled is required');
            return;
        }

        if(typeof errorCallback == 'undefined'){
            errorCallback = function(e){
                console.log('backgroundTask');
                console.log('errorCallback');
                console.log(e);
            };
        }

        if(typeof successCallback == 'undefined'){
            successCallback = function(result){
                console.log('backgroundTask');
                console.log('successCallback');
                console.log(result);
            };
        }

        exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'BackgroundTask', // mapped to our native Java class called "BackgroundTask"
            'enabled', // with this action name
            [{                  // and this array of custom arguments to create our entry
                "enabled":parseInt(isEnabled),
            }]
        );
     },
     removeUser: function(userId, successCallback, errorCallback) {

        if(typeof timeout == 'undefined'){
            timeout = 0;
        }

        if(typeof errorCallback == 'undefined'){
            errorCallback = function(e){
                console.log('backgroundTask');
                console.log('errorCallback');
                console.log(e);
            };
        }

        if(typeof successCallback == 'undefined'){
            successCallback = function(result){
                console.log('backgroundTask');
                console.log('successCallback');
                console.log(result);
            };
        }

        exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'BackgroundTask', // mapped to our native Java class called "BackgroundTask"
            'remove', // with this action name
            [{                  // and this array of custom arguments to create our entry
                "user": userId
            }]
        );
     }
}

module.exports = backgroundTask;