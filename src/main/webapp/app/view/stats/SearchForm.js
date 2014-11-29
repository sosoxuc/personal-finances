Ext.define('TR.view.stats.SearchForm', {
	extend : 'Ext.form.Panel',
	border: false,
	bodyPadding: 5,
	layout: 'hbox',
	constructor : function(cfg) {
		cfg = cfg || {};
		var me = this;

		var employeeStore = Ext.StoreManager.lookup('employeeList')
		|| Ext.create('TR.store.employee.List');
		var positionStore = Ext.StoreManager.lookup('positionStore') 
    	|| Ext.create('TR.store.employee.PositionStore');
	    var workplaceStore = Ext.StoreManager.lookup('workplaceStore') 
    	|| Ext.create('TR.store.employee.WorkplaceStore');
	
	    me.fieldDefaults = {
			labelWidth: 200,
			labelAlign: 'right',
			anchor: '80%'
		};
		
		cfg.grid.searchForm = me;
		
		var today = new Date();
		var startDate = new Date(today.getFullYear() + '-' + today.getMonth() + '-01');
		var endDate =  new Date(today.getFullYear() + '-' + (today.getMonth() + 1) + '-01');
		
		me.items = [
//		            {
//			xtype: 'combo',
//			fieldLabel: 'თანამშრომელი',
//			name: 'employee',
//			store: employeeStore,
//			labelWidth: 120,
//			editable: false,
//			displayField: 'fullName',
//			valueField: 'id'
//		}, 
		{
			xtype: 'fieldset',
			border: false,
			flex: 1,
			items: [{
				xtype: 'datefield',
				name: 'startDate',
				allowBlank: false,
				fieldLabel: 'დასაწყისი',
				value: startDate,
				emptyText: 'dd/mm/yyyy',
				format: 'd/m/Y'
			}, {
				xtype: 'datefield',
				name: 'endDate',
				allowBlank: false,
				fieldLabel: 'დასასრული',
				value: endDate,
				emptyText: 'dd/mm/yyyy',
				format: 'd/m/Y'
			}]
		}, {
			xtype: 'fieldset',
			border: false,
			flex: 1,
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
		
		me.submitFn = search;

		me.callParent(arguments);
		
		search();
		
		function search(){
			if(!me.getForm().isValid()) return ;
			var values = me.getForm().getValues();
			
			var params = {
					employeeId: values.employee,
					positionId: values.positionId,
					workplaceId: values.workplaceId,
					startDate: values.startDate,
					endDate: values.endDate
				};
			toEmptyString(params);
			correctDates(params, ['startDate', 'endDate']);
			
			cfg.grid.store.getProxy().extraParams = params;
			cfg.grid.store.load();
			cfg.grid.logParams = params;
			
		}
		
		function reset(){
			me.getForm().reset();
		}
		
		
	}
});