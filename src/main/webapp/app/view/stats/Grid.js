Ext.define('TR.view.stats.Grid', {
	extend : 'Ext.grid.Panel',
	constructor : function(cfg) {
		cfg = cfg || {};
		var me = this;

		me.store = Ext.StoreManager.lookup('statsStore')
			|| Ext.create('TR.store.stats.Store');
		
		me.tbar = [{
			text: 'ექსპორტი',
			href: 'rest/statistics/forPeriodExcel',
			hrefTarget: '_self',
			handler: exportGrid2
		}, {
			text: 'ტაბელი',
			href: 'rest/statistics/tabelExcel',
			hrefTarget: '_self',
			handler: exportGrid
		}, {
			text: 'ლოგი',
			handler: openLogs
		}]
		
		me.columns = [{
	    	xtype: 'templatecolumn',
	     	header: 'ფოტო',
	    	width: 60,
	    	tpl: '<img class="photo-mini" onclick="openPhoto(this);" src="rest/employee/getPhoto?_t={timestamp}&id={employee}" />'
	    }, {
			header: 'სახელი',
			dataIndex: 'fullName',
			flex: 2
		}, {
	    	header: 'სამუშაო ადგილი',
	    	dataIndex: 'workplace',
	    	flex: 1
	    }, {
	    	header: 'პოზიცია',
	    	dataIndex: 'position',
	    	flex: 1
	    }, {
			header: 'დასაწყისი',
			dataIndex: 'startDate',
			flex: 1,
			renderer: Ext.util.Format.dateRenderer('d-m-Y'),
	    	align: 'right'
		}, {
			header: 'დასასრული',
			dataIndex: 'endDate',
			flex: 1,
			renderer: Ext.util.Format.dateRenderer('d-m-Y'),
	    	align: 'right'
		}, {
			header : 'გაცდენა',
			dataIndex : 'misses',
			flex : 1,
	    	align: 'right'
		}, {
			header : 'დასწრება',
			dataIndex : 'minutesPresent',
			renderer : minutesToHours,
			flex : 1,
	    	align: 'right'
		}, {
			header : 'არდასწრება',
			dataIndex : 'minutesMissed',
			renderer : minutesToHours,
			flex : 1,
	    	align: 'right'
		}, {
			header : 'საპატიო',
			dataIndex : 'minutesForgived',
			renderer : minutesToHours,
			flex : 1,
	    	align: 'right'
		}, {
			header : 'სრული',
			dataIndex : 'minutesTotal',
			renderer : minutesToHours,
			flex : 1,
	    	align: 'right'
		}, {
	    	header: 'ანაზღაურება',
	    	dataIndex: 'hourlySalary',
	    	flex: 1,
	    	align: 'right'
	    } ];
		
		me.callParent(arguments);
		
		me.on('itemdblclick', openLogs);
		
		function openLogs(){
			
			var sel = me.getSelectionModel().getSelection();
	    	if(sel.length == 0) return ;
	    	rec = sel[0];
			
	    	var employeeId = rec.get('employee');
	    	var tab = Ext.getCmp('employee_' + employeeId);
	    	if(!tab){
	    		
	    		tab = Ext.create('TR.view.transactions.MainPanel', {
	    			employeeId: employeeId,
	    			closable: true,
	    			title: rec.get('fullName'),
	    			id: 'employee_' + employeeId,
	    			startDate: rec.get('startDate').getTime(),
	    			endDate: rec.get('endDate').getTime()
	    		});
	    		tr.tabPanel.add(tab);
	    		myRequest({
	    			url: 'rest/employee/get',
	    			params: {
	    				id: employeeId
	    			},
	    			callback: function(employee){
	    				tab.setEmployee(employee);
	    			}
	    		});
	    	}
	    	tr.tabPanel.setActiveTab(tab);
	    }
		
		me.openLogs = openLogs;

		function exportGrid(btn){
			btn.setHref("rest/statistics/tabelExcel?"+
					//"employeeId="+ values.employee + 
					"startDate="+ me.logParams.startDate +
					"&endDate="+ me.logParams.endDate +
					"&positionId="+ me.logParams.positionId + 
					"&workplaceId="+ me.logParams.workplaceId);
		}

		function exportGrid2(btn){
			btn.setHref("rest/statistics/forPeriodExcel?"+
				//"employeeId="+ values.employee + 
				"startDate="+ me.logParams.startDate +
				"&endDate="+ me.logParams.endDate +
				"&positionId="+ me.logParams.positionId + 
				"&workplaceId="+ me.logParams.workplaceId);
		}
	}
});
