let replyService = (function () {

    function add(reply, callback, error) {
        $.ajax({
            type: 'post',
            url: '/replies/new',
            data: JSON.stringify(reply),
            contentType: 'application/json; charset=utf-8',
            success: function (result, status, xhr) {
                if (callback) {
                    callback(result);
                }
            },
            error: function (xhr, status, er) {
                if (error) {
                    error(status);
                }
            }
        });
    }

    function getList(param, callback, error) {
        let bno = param.bno;
        let page = param.page || 1;

        $.getJSON('/replies/pages/' + bno + '/' + page,
            function (data) {
                if (callback) {
                    callback(data.replyCnt, data.list);
                }
            }).fail(function (xhr, status, err) {
                if (error) {
                    error();
                }
            });
    }

    function remove(rno, callback, error) {
        console.log("remove rno : " + rno);
        $.ajax({
            type: 'delete',
            url: '/replies/' + rno,
            success: function (result, status, xhr) {
                if (callback) {
                    callback(result);
                }
            },
            error: function (xhr, status, er) {
                if (error) {
                    error(er);
                }
            }
        });
    };

    function update(reply, callback, error) {
        console.log("update rno : " + reply.rno);
        $.ajax({
            type: 'put',
            url: '/replies/' + reply.rno,
            data: JSON.stringify(reply),
            contentType: 'application/json; charset=utf-8',
            success: function (result, status, xhr) {
                if (callback) {
                    callback(result);
                }
            },
            error: function (xhr, status, err) {
                if (error) {
                    error(er);
                }
            }
        });
    };

    function get(rno, callback, error) {
        console.log("get : " + rno);
        $.get('/replies/' + rno, function (result) {
            if (callback) {
                callback(result);
            }
        }).fail(function (xhr, status, err) {
            if (error) {
                error(er);
            }
        });
    };

    function displayTime(timeValue) {
        let today = new Date();
        let timeValueMilliseconds = Date.parse(timeValue);
        let gap = today.getTime() - timeValueMilliseconds;
        let oneDay = 24 * 60 * 60 * 1000;
        let dataObj = new Date(timeValueMilliseconds);

        if (gap < oneDay) {
            // display HH:MM:SS
            let hh = dataObj.getHours();
            let mm = dataObj.getMinutes();
            let ss = dataObj.getSeconds();
            return [(hh < 10 ? '0' : '') + hh,':', (mm < 10 ? '0' : '') + mm,':', (ss < 10 ? '0' : '') + ss].join('');
        } else {
            // display YYYY/MM/DD
            let yy = dataObj.getFullYear();
            let mm = dataObj.getMonth() + 1;
            let dd = dataObj.getDate();
            return [yy, '/', (mm < 10 ? '0' : '') + mm, '/', (dd < 10 ? '0' : '') + dd].join('');
        }
    }

    return {
        add: add,
        getList: getList,
        remove: remove,
        update: update,
        get: get,
        displayTime: displayTime
    };
})();

