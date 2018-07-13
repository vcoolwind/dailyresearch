package com.blackstone.dailyresearch.es;

import java.util.Map;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class MyPipeLine implements Pipeline {
    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String, Object> map = resultItems.getAll();
        PageContent content = new PageContent(map);
        //System.out.println(content);
        try {
            if(content.isValid()){
                ElasticUtils.writeJson("Website","domain",content.getID(),content.toMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
