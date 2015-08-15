Ext.define("TR.store.employees.EmployeeStore", {
    extend : "Ext.data.Store",
    storeId : 'employeeStore',
    // leadingBufferZone : 150,
    // sortable: false,
    // defaultSortable: false,
    // buffered : true,
    // remoteSort: false,
    // pageSize : 50,
    fields : [
            'id',
            'firstName',
            'lastName',
            'birthDate',
            'phone',
            'email',
            'username',
            'workplaceId',
            'workplaceName',
            'stateId',
            'positionId',
            'positionName',
            'userName',
            {
                name : 'createDate',
                convert : function(v) {
                    return v ? new Date(v) : '';
                }
            },
            {
                name : 'stateName',
                convert : function(v, rec) {
                    var st = rec.get('stateId');
                    return st == 1 ? 'აქტიური' : st == 0 ? 'გაუქმებული'
                            : st == 2 ? 'წაშლილი' : '';
                }
            } ],
    proxy : {
        url : 'rest/hr/employee/search',
        type : 'rest',
        reader : {
            type : 'json',
            root : 'list'
        }
    }
});