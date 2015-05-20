---
layout: default
title: StoRM Documentation Examples
---

# How-To

{% for doc in site.data.howto  %}
<div class="row-fluid marketing news-row">
  <div class="span4">
    <h4><a href="{{ site.baseurl }}{{ doc.relative-link }}">{{ doc.title }}</a></h4>
  </div>
  <div class="span8">
    {{ doc.description }}
    {% if doc.older-versions.size > 0 %}
    <p><i>Older versions</i> : {% for oldv in doc.older-versions %}<a href="{{ site.baseurl }}{{ oldv.relative-link }}">{{ oldv.name }}</a>{% if forloop.last %}{% else %}, {% endif %}{% endfor %}
    {% endif %}
    <br/><i><small>Last update: {{ doc.last-update }}</small></i>
  </div>
</div>
{% endfor %}