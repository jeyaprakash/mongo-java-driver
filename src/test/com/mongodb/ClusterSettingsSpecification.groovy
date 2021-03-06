package com.mongodb

import spock.lang.Specification

class ClusterSettingsSpecification extends Specification {
    def 'should set all properties'() {
        def hosts = [new ServerAddress('localhost'), new ServerAddress('localhost', 30000)]
        when:
        def settings = ClusterSettings.builder()
                                      .hosts(hosts)
                                      .mode(ClusterConnectionMode.Multiple)
                                      .requiredClusterType(ClusterType.ReplicaSet)
                                      .requiredReplicaSetName('foo').build();

        then:
        settings.hosts == hosts
        settings.mode == ClusterConnectionMode.Multiple
        settings.requiredClusterType == ClusterType.ReplicaSet
        settings.requiredReplicaSetName == 'foo'
    }

    def 'when cluster type is unknown and replica set name is specified, should set cluster type to ReplicaSet'() {
        when:
        def settings = ClusterSettings.builder().hosts([new ServerAddress()]).requiredReplicaSetName('yeah').build()

        then:
        ClusterType.ReplicaSet == settings.requiredClusterType
    }

    def 'connection mode should default to Multiple regardless of hosts count'() {
        when:
        def settings = ClusterSettings.builder().hosts([new ServerAddress()]).build()

        then:
        settings.mode == ClusterConnectionMode.Multiple
    }

    def 'when mode is Single and hosts size is greater than one, should throw'() {
        when:
        ClusterSettings.builder().hosts([new ServerAddress(), new ServerAddress('other')]).mode(ClusterConnectionMode.Single).build();
        then:
        thrown(IllegalArgumentException)

    }

    def 'when cluster type is Standalone and multiple hosts are specified, should throw'() {
        when:
        ClusterSettings.builder().hosts([new ServerAddress(), new ServerAddress('other')]).requiredClusterType(ClusterType.StandAlone)
                       .build();
        then:
        thrown(IllegalArgumentException)
    }

    def 'when a replica set name is specified and type is Standalone, should throw'() {
        when:
        ClusterSettings.builder().hosts([new ServerAddress(), new ServerAddress('other')]).requiredReplicaSetName('foo')
                       .requiredClusterType(ClusterType.StandAlone).build();
        then:
        thrown(IllegalArgumentException)
    }

    def 'when a replica set name is specified and type is Sharded, should throw'() {
        when:
        ClusterSettings.builder().hosts([new ServerAddress(), new ServerAddress('other')]).requiredReplicaSetName('foo')
                       .requiredClusterType(ClusterType.Sharded).build();
        then:
        thrown(IllegalArgumentException)
    }
}
