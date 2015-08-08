Ext.define("TR.view.employees.AddWindow", {
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

	    var positionStore = Ext.StoreManager.lookup('positionStore') || Ext.create('TR.store.employees.PositionStore');
	    var workplaceStore = Ext.StoreManager.lookup('workplaceStore') || Ext.create('TR.store.employees.WorkplaceStore');
    
	    var workplaceCombo = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel : 'სამუშაო ადგილი',
            name : 'workplaceId',
            store: workplaceStore,
            queryMode: 'local',
            flex: 1,
            editable: false,
            displayField: 'workplaceName',
            valueField: 'id'
	    });
	    
	    var positionCombo = Ext.create('Ext.form.field.ComboBox', {
        	store: positionStore,
        	editable: false,
        	queryMode: 'local',
        	flex: 1,
        	displayField: 'positionName',
        	valueField: 'id',
            fieldLabel : 'პოზიცია',
            name : 'positionId'
	    });
	    
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
	            fieldLabel : 'გვარი',
	            name : 'lastName'
	        }, {
	            fieldLabel : 'სახელი',
	            name : 'firstName'
	        }, {
	            xtype : 'datefield',
	            fieldLabel : 'დაბ. თარიღი',
	            allowBlank: true,
	            name : 'birthDate',
	            emptyText: 'dd/mm/yyyy',
	            format : 'd/m/Y'
	        }, {
	            fieldLabel : 'პირადი ნომერი',
	            name : 'personalNo',
	            regex: /[\d]{11}/,
	            maxLength: 11,
	            isEng: true,
	            enforceMaxLength: true,
	            maskRe: /[\d]/
	        }, {
	        	xtype: 'fieldcontainer',
	        	layout: 'hbox',
	        	items: [
	        	        positionCombo, {
	        		xtype: 'splitter'
	        	}, {
	        		xtype: 'button',
	        		text: '+',
	        		handler: addPosition
	        	}]
	        }, {
	        	xtype: 'fieldcontainer',
	        	layout: 'hbox',
	        	items: [
        	        workplaceCombo, {
    	        	xtype: 'splitter'
    	        }, {
    	        	xtype: 'button',
    	        	text: '+',
    	        	handler: addWorkplace
    	        }]
	        }, {
	            fieldLabel : 'ტელეფონი',
	            name : 'phone',
	            regex: /^5\d{8}$/,
	            allowBlank: true,
				enforceMaxLength: true,
				maxLength: 9,
				maskRe: /\d/
	        }, {
	            fieldLabel : 'ელ. ფოსტა',
	            vtype: 'email',
	            name : 'email',
	            allowBlank: true,
	            isEng: true
	        }, {
	        	xtype: 'checkbox',
	        	fieldLabel: 'ადმინი',
	        	name: 'userRole'
	        }]
	    });
	    

	    me.items = [ form ];

	    me.buttons = [];
	    
	    me.buttons.push({
	        text : cfg.edit ? 'რედაქტირება' : 'დამატება',
	        handler : cfg.edit ? edit : add
	    });

	    me.callParent(arguments);

	    function add() {
		    if (!form.getForm().isValid())
			    return;
		    var values = form.getForm().getValues();
		    delete values.id;
		    correctDates(values, ['birthDate', 'expireDate']);
		   
		    myRequest({
		        url : 'rest/hr/employee/add',
		        jsonData : values,
		        callback : function(id) {
			        cfg.searchForm.search();
		        	me.close();
		        }
		    });
	    }

	    function edit() {
	    	if (!form.getForm().isValid())
			    return;
		    var values = form.getForm().getValues();
		    values.userRole = values.userRole ? 2 : 1;
		    correctDates(values, ['birthDate', 'expireDate']);
		    
		    myRequest({
		        url : 'rest/employee/update',
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
	    				url: 'rest/hr/position/add',
	    				params: {
	    					name: text
	    				},
	    				callback: function(data){
	    					positionStore.load();
	    					positionCombo.setValue(data.id);
	    				}
	    			});
	    		}
	    	});
	    }
	    
	    function addWorkplace(){
	    	Ext.Msg.prompt("სამუშაო ადგილის დამატება", "სამუშაო ადგილი", function(ans, text){
	    		if(ans == 'ok' && text){
	    			myRequest({
	    				url: 'rest/hr/workplace/add',
	    				params: {
	    					name: text
	    				},
	    				callback: function(data){
	    					workplaceStore.load();
	    					workplaceCombo.setValue(data.id);
	    				}
	    			});
	    		}
	    	});
	    }
    }
});