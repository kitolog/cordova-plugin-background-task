var exec = require('cordova/exec');

var backgroundTask = {
    addTask: function(taskName, taskCallback, successCallback, errorCallback, frequency) {

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
            'addTask', // with this action name
            [{                  // and this array of custom arguments to create our entry
                "frequency": frequency,
                "taskName": taskName,
                "taskCallback": taskCallback,
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
            'removeTask', // with this action name
            [{                  // and this array of custom arguments to create our entry
                "taskName": taskName
            }]
        );
     }
}

module.exports = backgroundTask;