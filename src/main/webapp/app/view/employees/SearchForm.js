Ext.define("TR.view.employees.SearchForm", {
    extend : "Ext.form.Panel",
    bodyPadding: 5,
    border: false,
    layout: 'hbox',
    fieldDefaults: {
    	labelAlign: 'right',
    	labelWidth: 130
    },
    constructor : function(cfg) {
	    cfg = cfg || {};
	    var me = this;

	    cfg.grid.searchForm = me;
	    
	    var store = Ext.create('Ext.data.Store', {
	    	fields: ['id', 'name'],
	    	data: [{id: 1, name: 'აქტიური'},
	    	       {id: 0, name: 'გაუქმებული'},
	    	       {id: 2, name: 'წაშლილი'}]
	    });
	    var positionStore = Ext.StoreManager.lookup('positionStore') 
    	|| Ext.create('TR.store.employees.PositionStore');
	    var workplaceStore = Ext.StoreManager.lookup('workplaceStore') 
    	|| Ext.create('TR.store.employees.WorkplaceStore');
	    
	    me.submitFn = search;
	    me.items = [{
	    	xtype: 'fieldset',
	    	flex: 1,
	    	border: false,
	    	items: [{
	    		xtype: 'textfield',
	    		name: 'lastName',
	    		fieldLabel: 'გვარი'
	    	}, {
	    		xtype: 'textfield',
	    		name: 'firstName',
	    		fieldLabel: 'სახელი'
	    	}]
	    }, {
	    	xtype: 'fieldset',
	    	flex: 1,
	    	border: false,
	    	items: [{
	    		xtype: 'textfield',
	    		name: 'personalNo',
	    		fieldLabel: 'პირადი ნომერი',
	    		regex: /[\d]{11}/,
	            maxLength: 11,
	            isEng: true,
	            enforceMaxLength: true,
	            maskRe: /[\d]/
	    	}, {
	    		xtype: 'combo',
	    		editable: false,
	    		name: 'state',
	    		queryMode: 'local',
	    		fieldLabel: 'სტატუსი',
	    		store: store,
	    		displayField: 'name',
	    		valueField: 'id'
	    	}]
	    }, {
	    	xtype: 'fieldset',
	    	flex: 1,
	    	border: false,
	    	items: [{
	    		xtype: 'combo',
	    		name: 'positionId',
	    		fieldLabel: 'პოზიცია',
	    		queryMode: 'local',
	    		editable: false,
	    		store: positionStore,
	    		displayField: 'positionName',
	    		valueField: 'id'
	    	}, {
	    		xtype: 'combo',
	    		name: 'workplaceId',
	    		fieldLabel: 'სამუშაო ადგილი',
	    		queryMode: 'local',
	    		editable: false,
	    		store: workplaceStore,
	    		displayField: 'workplaceName',
	    		valueField: 'id'
	    	}]
	    }];

	    me.buttons = [{
	    	text: 'ძებნა',
	    	handler: search
	    }, {
	    	text: 'გასუფთავება',
	    	handler: reset
	    }];
	    
	    me.callParent(arguments);
	    
	    me.search = search;
	    
	    function search(sorter){
	    	var values = me.getForm().getValues();
	    	if(sorter){
	    		values.sort = sorter.property;
	    		values.dir = sorter.direction;
	    	}
	    	cfg.grid.store.getProxy().extraParams = values;
	    	cfg.grid.store.load();
	    	cfg.grid.resetButtons();
	    }
	    
	    function reset(){
	    	me.getForm().reset();
	    }
    }
});