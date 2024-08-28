import Ember from 'ember';

export default Ember.Component.extend({
  actions: {
    show(lists) {
      alert(lists);
    }
  }

});
