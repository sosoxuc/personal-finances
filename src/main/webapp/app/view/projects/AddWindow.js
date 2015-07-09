Ext.define("TR.view.projects.AddWindow", {
    extend : "Ext.window.Window",
    modal : true,
    width : 400,
    height: 350,
    autoShow : true,
    layout : 'border',
    constructor : function(cfg) {
	    cfg = cfg || {};
	    var me = this;

	    me.title = cfg.edit ? 'რედაქტირება' : 'დამატება';

	   
	    
	    var form = Ext.create('Ext.form.Panel', {
	    	border: false,
	    	split: true,
	    	//width: 350,
	    	region: 'center',
	    	bodyPadding: 5,
	        fieldDefaults : {
	            labelWidth : 150,
	            labelAlign : 'right',
	            anchor : '100%',
	            allowBlank: false
	        },
	        defaultType : 'textfield',
	        items : [ {
	            fieldLabel : 'დასახელება',
	            name : 'projectName'
	        }]
	    });
	    

	    me.items = [ form ];

	    me.buttons = [{
	        text : cfg.edit ? 'რედაქტირება' : 'დამატება',
	        handler : cfg.edit ? edit : add
	    }];
	    
	    me.callParent(arguments);

	    function add() {
		    if (!form.getForm().isValid())
			    return;
		    var values = form.getForm().getValues();
		    
		    myRequest({
		        url : 'rest/project/create',
		        params : values,
		        callback : function(id) {
		            cfg.grid.store.load();
		            cfg.grid.getSelectionModel().deselectAll();
		        	me.close();
		        }
		    });
	    }

	    function edit() {
	    	if (!form.getForm().isValid())
			    return;
		    var values = form.getForm().getValues();
		    myRequest({
		        url : 'rest/project/update',
		        params : values,
		        callback : function(id) {
			        cfg.searchForm.search();
		        	me.close();
		        }
		    });
	    }
	    
	    function addPosition(){
	    	Ext.Msg.prompt("პოზიციის დამატება", "პოზიცია", function(ans, text){
	    		if(ans == 'ok' && text){
	    			myRequest({
	    				url: 'rest/employee/addPosition',
	    				params: {
	    					name: text
	    				},
	    				callback: function(id){
	    					if(id) positionStore.load();
	    					positionCombo.setValue(id);
	    				}
	    			});
	    		}
	    	});
	    }
	    
	    function addWorkplace(){
	    	Ext.Msg.prompt("სამუშაო ადგილის დამატება", "სამუშაო ადგილი", function(ans, text){
	    		if(ans == 'ok' && text){
	    			myRequest({
	    				url: 'rest/employee/addWorkplace',
	    				params: {
	    					name: text
	    				},
	    				callback: function(id){
	    					if(id) workplaceStore.load();
	    					workplaceCombo.setValue(id);
	    				}
	    			});
	    		}
	    	});
	    }
    }
});