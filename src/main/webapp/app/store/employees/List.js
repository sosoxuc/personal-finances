Ext.define("TR.store.employees.List", {
    extend : "Ext.data.Store",
    storeId: 'employeeList',
    autoLoad: true,
    fields: ['id', 'personalNo', 'firstName', 'lastName', 'fullName', 'hourlySalary', {
    	name: 'birthDate', 
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
    	name: 'stateName',
    	convert: function(v, rec){
    		var st = rec.get('state');
    		return st == 1 ? 'აქტიური' : st == 0 ? 'გაუქმებული' : st == 2 ? 'წაშლილი' : '';
    	}
    }],
    proxy: {
    	url: 'rest/employee/getAll',
    	type: 'rest',
    	reader: {
    		type: 'json'
    	}
    }
});