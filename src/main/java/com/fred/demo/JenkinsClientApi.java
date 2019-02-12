package com.fred.demo;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueReference;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuxiaokun
 * @version 0.0.1
 * @since 2019/2/11
 */
public class JenkinsClientApi {

    public static void main(String[] args) throws Exception {

        JenkinsServer jenkins = new JenkinsServer(new URI("http://192.168.1.12:8080/"), "root", "123456");

        Map<String, Job> jobs = jenkins.getJobs();
        System.out.println(jobs.size());

        JobWithDetails job = jobs.get("bigtour-v2-optional").details();
        Map<String, String> params = new HashMap<>(1);
        params.put("module", "nmm");
        QueueReference queueReference = job.build(params, true);

        BuildResult result = jenkins.getJob("bigtour-v2-optional").getLastBuild().details().getResult();

    }
}
