package com.zabogonski;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GitHubJob {

    private final GitHub gitHub;
    private final Gui gui;
    private final Set<Long> allPrIds = new HashSet<>();

    {
        try {
            gui = new Gui();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public GitHubJob() {
        try {
            gitHub = new GitHubBuilder().withAppInstallationToken(System.getenv("GITHUB_TOKEN")).build();
            System.out.println("Connection successful");
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException{
        GHMyself myself = gitHub.getMyself();
        String login = myself.getLogin();
        System.out.println("Connection to " + login);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    HashSet<GHPullRequest> newPrs = new HashSet<>();
                    List<RepositoryDescription> repos = myself.getAllRepositories()
                            .values()
                            .stream()
                            .map(repository -> {
                                try {
                                    List<GHPullRequest> prs = repository.queryPullRequests()
                                            .list()
                                            .toList();
                                    Set<Long> prIds = prs.stream()
                                            .map(GHPullRequest::getId).collect(Collectors.toSet());
                                    prIds.removeAll(allPrIds);
                                    allPrIds.addAll(prIds);
                                    prs.forEach(pr -> {
                                        if (prIds.contains(pr.getId())) {
                                            newPrs.add(pr);

                                        }
                                    });
                                    return new RepositoryDescription(
                                            repository.getFullName(),
                                            repository,
                                            prs
                                    );
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }).collect(Collectors.toList());
                    gui.setMenu(login, repos);
                    newPrs.forEach(pr -> {
                        gui.showNotification("New PR in " + pr.getRepository().getFullName(), pr.getTitle());
                    });
                } catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        },1000, 1000);
    }
}
