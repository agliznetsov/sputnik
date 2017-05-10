import React from 'react';
import * as _ from 'lodash';

import './dataSource.css';
import httpService from '../services/HttpService';
import notificationService from '../services/NotificationService';
import DataSourceModal from './DataSourceModal';

class DataSources extends React.Component {

    constructor() {
        super();
        this.state = {
            showModal: false,
            dataProfiles: [],
            allSources: [],
            sources: [],
            filter: {}
        };
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    componentDidMount() {
        let that = this;
        httpService.get("/dataProfiles").then(response => {
            that.state.dataProfiles = response.data.map(it => it.name);
        });
        this.refresh();
    }

    refresh() {
        let that = this;
        httpService.get("/dataSources").then(function (response) {
            that.state.allSources = response.data;
            that.filter();
        })
    }

    filter() {
        let f = this.state.filter;
        let sources = _.filter(this.state.allSources, function (it) {
            return (
                (!f.groupName || f.groupName === it.groupName)
                && (!f.name || it.name.indexOf(f.name) >= 0)
                && (!f.profile || it.dataProfileName.indexOf(f.profile) >= 0)
                && (!f.url || it.url.indexOf(f.url) >= 0)
            );
        });
        this.state.sources = _.orderBy(sources, ['groupName', 'name']);
        this.forceUpdate();
    }

    addSource() {
        this.editSource({
            enabled: true
        });
    }

    editSource(source) {
        this.state.source = JSON.parse(JSON.stringify(source));
        this.state.showModal = true;
        this.forceUpdate();
    }

    saveSource() {
        let that = this;
        httpService.post("/dataSources/", this.state.source).then(ok => that.refresh(), err => notificationService.error(err.data.message));
        this.closeModal();
    }

    deleteSource() {
        let that = this;
        httpService.delete("/dataSources/" + this.state.source.id).then(ok => that.refresh(), err => notificationService.error(err.data.message));
        this.closeModal();
    }

    closeModal() {
        this.state.source = null;
        this.state.showModal = false;
        this.forceUpdate();
    }

    collect() {
        let that = this;
        httpService.post("/collect").then(response => {
                setTimeout(that.refresh, 1000);
            },
            error => {
                notificationService.error(error.data.message);
            }
        );
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.state.filter[name] = value;
        this.filter();
    }

    render() {
        return (
            <div className="app-body settings">
                {this.menu()}
                {this.table()}
                {this.state.showModal &&
                <DataSourceModal show={this.state.showModal} model={this.state.source} dataProfiles={this.state.dataProfiles} onClose={() => this.closeModal()}
                                 onOk={() => this.saveSource()} onDelete={() => this.deleteSource()}/>
                }
            </div>
        )
    };

    menu() {
        return (
            <div className="well well-sm">
                <div className="btn-group">
                    <button className="btn btn-sm" onClick={() => this.refresh()} title="Refresh"><i
                        className="fa fa-refresh"></i></button>
                    <button className="btn btn-sm" onClick={() => this.addSource()} title="Add new data source"><i
                        className="fa fa-plus"></i></button>
                </div>
                <div className="btn-group">
                    <button className="btn btn-sm" onClick={() => this.collect()} title="Force data collection"><i
                        className="fa fa-gears"></i></button>
                </div>
                <div className="pull-right">
                    <label>Showing {this.state.sources.length} of {this.state.allSources.length}</label>
                </div>
            </div>
        )
    }

    table() {
        return (
            <table className="table table-striped table-bordered table-hover">
                <thead>
                <tr>
                    <th width="50px" className="text-align-center">
                        <label>Enabled</label>
                    </th>
                    <th>
                        <label>Group</label>
                        <input name="groupName" className="form-control input-sm" type="text"
                               value={this.state.filter.groupName} onChange={this.handleInputChange}/>
                    </th>
                    <th>
                        <label>Name</label>
                        <input name="name" className="form-control input-sm" type="text" value={this.state.filter.name}
                               onChange={this.handleInputChange}/>
                    </th>
                    <th>Description</th>
                    <th>
                        <label>Profile</label>
                        <input name="profile" className="form-control input-sm" type="text"
                               value={this.state.filter.profile} onChange={this.handleInputChange}/>
                    </th>
                    <th>
                        <label>URL</label>
                        <input name="url" className="form-control input-sm" type="text" value={this.state.filter.url}
                               onChange={this.handleInputChange}/>
                    </th>
                    <th>Status</th>
                    <th>Ping, ms</th>
                </tr>
                </thead>
                <tbody>
                {this.state.sources.map((it) => this.tableRow(it))}
                </tbody>
            </table>
        )
    }

    tableRow(source) {
        let icon, title;
        let enabledClass = 'fa ' + (source.enabled ? 'fa-check-square-o' : 'fa-square-o');
        if (source.status) {
            icon = 'fa ' + (source.status.ok ? 'fa-check-circle black' : 'fa-exclamation-triangle red');
            if (source.status.ok) {
                let moment = window.moment(source.status.updated * 1000);
                title = moment.format('ll') + ' ' + moment.format('HH:mm');
            } else {
                title = source.status.errorMessage;
            }

        }
        return (
            <tr key={source.id} onClick={() => this.editSource(source)}>
                <td><i className={enabledClass}></i></td>
                <td>{source.groupName}</td>
                <td>{source.name}</td>
                <td>{source.description}</td>
                <td>{source.dataProfileName}</td>
                <td>{source.url}</td>
                <td>
                    <i className={icon}></i>
                    <span className="li-text">{title}</span>
                </td>
                <td>{source.status ? source.status.ping : ''}</td>
            </tr>
        )
    }
}

export default DataSources;
