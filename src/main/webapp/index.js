var tr = tr || {};

Ext.Loader.setConfig({
	enabled: true
});

Ext.application({
	name: 'TR',
	appFolder: 'app',
	launch: loadPassport 
});

var passport;

function loadPassport() {
    myRequest({
        url: 'rest/security/passport',
        method: 'GET',
        callback: function(data){
            if(data && data.valid){
                passport = data
                loadLanguage()
            }else{
                location.href = 'login.html';
            }
        }
    });
}

function loadLanguage() {
    
    var language = passport.employee.language;
    var userLanguage = language ? language : Config.SYSTEM_LANG;
    
    var element = document.createElement("script")
    element.type="text/javascript"
    element.src = "lang/" + userLanguage + ".js"
    //element.onload = launch
    element.addEventListener('load', launch)
    document.body.appendChild(element)
}

function launch(){
    var viewport = Ext.create('Ext.container.Viewport', {
        layout: 'fit',
        items: [ ]
    });
    
    viewport.add(Ext.create('TR.view.MainPanel', {
        employee: passport.employee,
        passport: passport
    }));
}
