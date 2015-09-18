var exec = require('cordova/exec');

var backgroundTask = {
    addTask: function(taskName, taskCallback, successCallback, errorCallback, frequency) {

        if(typeof taskName != 'string'){
            console.error('taskName is required');
            return;
        }

        if(typeof taskCallback != 'function'){
            console.error('taskCallback is required and must be a function');
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
                "name": taskName,
                "callback": taskCallback.toString(),
            }]
        );
     },
     removeTask: function(taskName, successCallback, errorCallback) {

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
                "name": taskName,
                "frequency": 0,
                "callback": "",
            }]
        );
     }
}

module.exports = backgroundTask;