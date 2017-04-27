import Ember from 'ember';
import route from 'ember-redux/route';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
import { asyncRequestBrowseData } from 'wherehows-web/actions/browse';

const { Route } = Ember;
// TODO: DSS-6581 Create URL retrieval module
// TODO: Route should transition to browse/entity, pay attention to the fact that
//   this route initializes store with entity metrics on entry
const entityUrls = {
  datasets: '/api/v1/datasets?size=10',
  metrics: '/api/v1/metrics?size=10',
  flows: '/api/v1/flows?size=10'
};

export default route({
  model: (dispatch, { entity = 'datasets', page = '1' }) => dispatch(asyncRequestBrowseData(page, entity, entityUrls))
})(
  Route.extend(AuthenticatedRouteMixin, {
    /**
     * Browse route does not render any content,  but hydrates the store with initial data transition to child route
     */
    afterModel() {
      this.transitionTo('browse.entity', 'datasets');
    }
  })
);
