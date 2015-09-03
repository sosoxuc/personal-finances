var tr = tr || {};

Ext.Loader.setConfig({
    enabled: true
});

Ext.application({
    name: 'TR',
    appFolder: 'app',
    launch: launch 
});

function launch(passport){
    var viewport = Ext.create('Ext.container.Viewport', {
        layout: 'fit',
        items: [ Ext.create('TR.view.LoginPanel') ]
    });
}
