var tr = tr || {};

Ext.Loader.setConfig({
	enabled: true
});

Ext.application({
	name: 'TR',
	appFolder: 'app',
	launch: function(){
		
		
		var viewport = Ext.create('Ext.container.Viewport', {
			layout: 'fit',
			items: [ ]
		});
		
		if(tr.login){
			viewport.add(Ext.create('TR.view.LoginPanel'));
		} else {
			myRequest({
				url: 'rest/security/passport',
				callback: function(passport){
					log(passport);
					if(passport.valid){
						tr.employee = passport.employee;
						viewport.add(Ext.create('TR.view.MainPanel', {
							employee: passport.employee
						}));
					}else{
						location.href = 'login.html';
					}
					
				}
			});
		}
	}
});