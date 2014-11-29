Ext.define('TR.view.stats.MainPanel', {
	extend : 'Ext.panel.Panel',
	border: false,
	layout: 'border',
	constructor : function(cfg) {
		cfg = cfg || {};
		var me = this;

		var grid = Ext.create('TR.view.stats.Grid', {
			region: 'center'
		});
		
		var searchForm = Ext.create('TR.view.stats.SearchForm', {
			region: 'north',
			grid: grid
		});
		
		me.items = [ searchForm, grid ];

		me.callParent(arguments);
	}
});