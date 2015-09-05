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
                loadAppearance()
                loadLanguage()
            }else{
                location.href = 'login.html';
            }
        }
    });
}

function loadAppearance() {
    var appearance = passport.employee.appearance;
    var userAppearance = appearance ? appearance : Config.SYSTEM_APPEARANCE;
    Ext.each(appearances, function(item){
        if (item.name == userAppearance) {
            var element=document.createElement("link")
            element.setAttribute("rel", "stylesheet")
            element.setAttribute("type", "text/css")
            element.setAttribute("href", item.url)
            document.getElementsByTagName("head")[0].appendChild(element)
        }
    })
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
