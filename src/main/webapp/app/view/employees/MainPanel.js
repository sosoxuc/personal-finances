Ext.define("TR.view.employees.MainPanel", {
    extend : "Ext.panel.Panel",
    border: false,
    layout: 'border',
    title : LANG.EMPLOYEES,
    constructor : function(cfg) {
	    cfg = cfg || {};
	    var me = this;
	    
	    var grid = Ext.create('TR.view.employees.EmployeeGrid', {
	    	region: 'center'
	    });
	    var searchForm = Ext.create('TR.view.employees.SearchForm', {
	    	region: 'north',
	    	grid: grid
	    });
	    
	    
	    me.items = [ grid ];

	    me.callParent(arguments);
	    
	    searchForm.search();
    }
});