<!-- Copyright:    -->
 <!-- MailList.html     -->
 <!-- @author:      liushilei  -->
 <!-- @version:     V1.0             -->
 <!-- @Date:        2018-06-20 14:41:28      -->

<#include "/base/base.ftl">
  <div class="centerRowBg centerRowBg_admin">
      <div class="row">
          <div class="col-lg-12">
              <h3 class="page-header">邮件发送记录</h3>
          </div>
      </div>
      <div class="row">
          <div class="_params">
          </div>
      </div>
      <div class="row">
          <div class="col-md-12">
              <form id="table_query_form">
                  <div class="row">
                      <div class="col-md-2 column">
                          <div class="form-group">
                              <label for="langName">接收人</label>
                              <input type="text" class="form-control" name="address" id="address" tablesearch/>
                          </div>
                      </div>
                      <div class="col-md-2 column">
                          <div style="margin-top: 26px;">
                              <button type="button" id="table_query" class="btn btn-primary"><i
                                      class="glyphicon glyphicon-search"></i>查询
                              </button>
                              <button type="button" id="table_reset" class="btn btn-primary"><i
                                      class="glyphicon glyphicon-refresh"></i>重置
                              </button>
                          </div>
                      </div>
                  </div>
              </form>
              <div id="toolbar">
                  <button id="resend" class="btn btn-info-blue" >
                      <i class="glyphicon glyphicon-plus"></i>重新发送
                  </button>
                  <button id="remove" class="btn btn-info-blue" >
                      <i class="glyphicon glyphicon-remove"></i>删除
                  </button>
              </div>
              <table id="table"
                     data-toolbar="#toolbar"
                     data-show-refresh="true"
                     data-show-columns="false"
                     data-show-export="false"
                     data-search="false"
                     data-detail-view="false"
                     data-minimum-count-columns="2"
                     data-pagination="true"
                     data-id-field="id"
                     data-page-size="20"
                     data-page-list="[10, 25, 50, 100]"
                     data-show-footer="false"
                     data-side-pagination="server"
              >
              </table>
          </div>
      </div>
  </div>

 <script type="text/javascript">
     seajs.use("js/admin/web/Mail", function (o) {
         o.list();
     });
 </script>

