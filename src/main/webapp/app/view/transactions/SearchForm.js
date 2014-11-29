Ext.define("TR.view.transactions.SearchForm", {
    extend : "Ext.form.Panel",
    border: false,
    bodyPadding: 5,
    layout: 'hbox',
    fieldDefaults: {
    	labelAlign: 'right'
    },
    constructor : function(cfg) {
	    cfg = cfg || {};
	    var me = this;
	    
	    cfg.grid.searchForm = me;
	    
	    me.defaults = {
	    	margin: '0 10 0 0'
	    };
	    me.items = [{
	    	xtype: 'datefield',
	    	fieldLabel: 'დასაწყისი',
	    	name: 'startDate',
	    	format: 'd/m/Y',
	    	value: cfg.startDate ? new Date(cfg.startDate) : ''
	    }, {
	    	xtype: 'datefield',
	    	fieldLabel: 'დასასრული',
	    	name: 'endDate',
	    	format: 'd/m/Y',
	    	value: cfg.endDate ? new Date(cfg.endDate) : ''
	    }, {
	    	xtype: 'button',
	    	text: 'ფილტრი',
	    	handler: filter
	    }];

	    me.callParent(arguments);

	    me.filter=filter;
	    
	    function filter(){
	    	var values = me.getForm().getValues();
	    	correctDates(values, ['startDate', 'endDate']);
	    	me.searchedValues = values;
	    	log(me.searchedValues);
			cfg.grid.load(values.startDate, values.endDate);
	    }
	    
	    function exportGrid(btn){
			btn.setHref("rest/log/dailyLogExport?" +
					"employee="+ cfg.employeeId + 
					"&startDate="+ me.searchedValues.startDate +
					"&endDate="+ me.searchedValues.endDate);
		}
    }
});