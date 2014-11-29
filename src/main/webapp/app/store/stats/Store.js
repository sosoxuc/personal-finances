Ext.define('TR.store.stats.Store', {
	extend : 'Ext.data.Store',
	storeId : 'statsStore',
	fields : [ 'fullName', {
		name : 'startDate',
		convert : function(v, rec) {
			return v ? new Date(v) : '';
		}
	}, {
		name : 'endDate',
		convert : function(v, rec) {
			return v ? new Date(v) : '';
		}
	}, {
    	name: 'timestamp',
    	convert: function(){
    		return new Date().getTime();
    	}
    }, 'employee', 'holyDays', 'vacations', 'misses', 'presents',
			'minutesPresent', 'minutesMissed', 'minutesForgived','hourlySalary',
			'minutesTotal', 'position', 'workplace' ],
	proxy : {
		type : 'rest',
		url : 'rest/statistics/forPeriod',
		reader : {
			type : 'json'
		}
	}
});