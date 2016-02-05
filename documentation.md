---
layout: default
title: StoRM Documentation
---

## ![Books]({{site.baseurl}}/assets/images/books.png) Documentation

##### _Administrator guides_

{% for doc in site.data.docs.administrator-guides  %}
<div class="row-fluid marketing news-row">
  <div class="span4"><a href="{{site.baseurl}}{{doc.relative-link}}"><h4>{{doc.title}}</h4></a></div>
  <div class="span8">
    {% if doc.older-versions.size > 0 %}
      {{ doc.description }}
      <br/><i>Older versions</i>: {% for oldv in doc.older-versions %}<a href="{{site.baseurl}}{{oldv.relative-link}}">{{oldv.name}}</a>{% if forloop.last %}{% else %}, {% endif %}{% endfor %}
      <br/><i><small>Last update: {{ doc.last-update }}</small></i>
    {% else %}
      {{ doc.description }}
      <br/><i><small>Last update: {{doc.last-update}}</small></i>
    {% endif %}
  </div>
</div>
{% endfor %}


##### _User guides_

{% for doc in site.data.docs.user-guides  %}
<div class="row-fluid marketing news-row">
  <div class="span4"><a href="{{site.baseurl}}{{doc.relative-link}}"><h4>{{doc.title}}</h4></a></div>
  <div class="span8">
    {% if doc.older-versions.size > 0 %}
      {{ doc.description }}
      <br/><i>Older versions</i>: {% for oldv in doc.older-versions %}<a href="{{ site.baseurl }}{{ oldv.relative-link }}">{{ oldv.name }}</a>{% if forloop.last %}{% else %}, {% endif %}{% endfor %}
      <br/><i><small>Last update: {{doc.last-update}}</small></i>
    {% else %}
      {{ doc.description }}
      <br/><i><small>Last update: {{ doc.last-update }}</small></i>
    {% endif %}
  </div>
</div>
{% endfor %}
