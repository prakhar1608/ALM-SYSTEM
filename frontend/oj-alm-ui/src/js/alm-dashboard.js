/**
 * Oracle JET 10 view-model starter. Add this module to a JET 10 application's
 * viewModels folder and bind it to an oj-table / oj-form-layout view.
 */
define(['knockout', 'ojs/ojarraydataprovider'], function (ko, ArrayDataProvider) {
  'use strict';
  function DashboardViewModel() {
    var self = this;
    self.metrics = ko.observable({});
    self.rows = ko.observableArray([]);
    self.dataProvider = new ArrayDataProvider(self.rows, { keyAttributes: 'SCENARIO_ID' });
    self.load = async function () {
      var authorization = sessionStorage.getItem('almAuthorization');
      var response = await fetch('/alm/api/dashboard', { headers: { Authorization: authorization } });
      if (!response.ok) throw new Error('Unable to load ALM dashboard');
      var data = await response.json();
      self.metrics(data);
      self.rows(data.latestScenarios || []);
    };
  }
  return DashboardViewModel;
});
