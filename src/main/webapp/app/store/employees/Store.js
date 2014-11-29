Ext.define("TR.store.employees.Store", {
    extend : "Ext.data.Store",
    storeId: 'employeeStore',
//    leadingBufferZone : 150,
//    sortable: false,
//    defaultSortable: false,
//	buffered : true,
//    remoteSort: false,
//	pageSize : 50,
    fields: ['id', 'date', 'firstName', 'lastName', 
             'type', 'typeId', 'userRole', 'hourlySalary', {
    	name: 'date', 
    	convert: function(v){
    		return v ? new Date(v) : '';
    	}
    }, 'phone', 'email', 'workplace', 'state',
             'position', {
    	name: 'createDate',
    	convert: function(v){
    		return v ? new Date(v) : '';
    	}
    }, {
    	name: 'expireDate',
    	convert: function(v){
    		return v ? new Date(v) : '';
    	}
    }, {
    	name: 'stateName',
    	convert: function(v, rec){
    		var st = rec.get('state');
    		return st == 1 ? 'აქტიური' : st == 0 ? 'გაუქმებული' : st == 2 ? 'წაშლილი' : '';
    	}
    }, {
    	name: 'timestamp',
    	convert: function(){
    		return new Date().getTime();
    	}
    }, {
    	name: 'fullName',
    	convert: function(v, rec){
    		return rec.get('lastName') + ' ' + rec.get('firstName');
    	}
    }],
    proxy: {
    	url: 'rest/employee/search',
    	type: 'rest',
    	reader: {
    		type: 'json',
    		root: 'list'
    	}
    }
});