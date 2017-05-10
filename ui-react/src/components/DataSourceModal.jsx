import React from 'react';

import {Modal, Button} from 'react-bootstrap';

class DataSourceModal extends React.Component {

    constructor(props) {
        super(props);
        this.state = {dataFormats: ['JSON', 'PROPERTIES']};
        if (!this.props.model.dataFormat) {
            this.props.model.dataFormat = this.state.dataFormats[0];
        }
        if (!this.props.model.dataProfileName) {
            this.props.model.dataProfileName = this.props.dataProfiles[0];
        }
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    isValid() {
        return true;
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.id;
        this.props.model[name] = value;
        this.forceUpdate();
    }

    render() {
        return (
            <Modal show={this.props.show} onHide={this.props.onClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Edit data source</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <form className="form-horizontal" name="form">
                        <div className="form-group">
                            <label className="col-md-4 control-label" htmlFor="groupName">Group name</label>
                            <div className="col-md-8">
                                <input className="form-control" type="text" id="groupName"
                                       value={this.props.model.groupName} onChange={this.handleInputChange}/>
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-md-4 control-label" htmlFor="name">Name</label>
                            <div className="col-md-8">
                                <input className="form-control" type="text" id="name" name="name" value={this.props.model.name}
                                       onChange={this.handleInputChange}/>
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-md-4 control-label" htmlFor="name">Description</label>
                            <div className="col-md-8">
                                <textarea className="form-control" type="text" id="description"
                                          value={this.props.model.description} onChange={this.handleInputChange}/>
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-md-4 control-label" htmlFor="url">URL</label>
                            <div className="col-md-8">
                                <input className="form-control" type="text" id="url" value={this.props.model.url}
                                       onChange={this.handleInputChange}/>
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-md-4 control-label" htmlFor="dataProfile">Data profile</label>
                            <div className="col-md-8">
                                <select id="dataProfile" value={this.props.model.dataProfileName}
                                        onChange={this.handleInputChange} className="form-control">
                                    {this.props.dataProfiles.map(it => <option key={it} value={it}>{it}</option>)}
                                </select>
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-md-4 control-label" htmlFor="dataFormat">Data format</label>
                            <div className="col-md-8">
                                <select id="dataFormat" value={this.props.model.dataFormat}
                                        onChange={this.handleInputChange} className="form-control">
                                    {this.state.dataFormats.map(it => <option key={it} value={it}>{it}</option>)}
                                </select>
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-md-4 control-label" htmlFor="enabled">Enabled</label>
                            <div className="col-md-8">
                                <input type="checkbox" id="enabled" checked={this.props.model.enabled}
                                       onChange={this.handleInputChange} style={{height: '25px'}}/>
                            </div>
                        </div>
                    </form>
                </Modal.Body>
                <Modal.Footer>
                    {this.props.model.id &&
                    <Button bsStyle="danger" className="pull-left" onClick={this.props.onDelete}
                            title="Remove data source"><i className="fa fa-trash"></i></Button>
                    }
                    <Button bsStyle="primary" onClick={this.props.onOk} disabled={!this.isValid()}>OK</Button>
                    <Button bsStyle="default" onClick={this.props.onClose}>Cancel</Button>
                </Modal.Footer>
            </Modal>
        );
    }
}

export default DataSourceModal;
